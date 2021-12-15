package com.kisswe.scotland.service;

import com.kisswe.scotland.client.KakaoOauthClient;
import com.kisswe.scotland.client.domain.KakaoAuthenticateResponse;
import com.kisswe.scotland.client.domain.KakaoProfileResponse;
import com.kisswe.scotland.client.domain.KakaoTokenInfoResponse;
import com.kisswe.scotland.database.Oauth;
import com.kisswe.scotland.database.Platform;
import com.kisswe.scotland.database.User;
import com.kisswe.scotland.repository.OauthRepository;
import com.kisswe.scotland.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoAuthService {
    private static final Platform PLATFORM = Platform.KAKAO;
    private final KakaoOauthClient kakaoOauthClient;
    private final OauthRepository oauthRepository;
    private final UserRepository userRepository;

    @Transactional
    public Mono<User> authenticate(String authCode) {
        Mono<String> accessTokenMono = Mono.just(authCode)
                .flatMap(kakaoOauthClient::authenticate)
                .doOnNext(kakaoAuthenticateResponse -> log.info("Kakao Authentication Info = {}", kakaoAuthenticateResponse))
                .map(KakaoAuthenticateResponse::getAccessToken)
                .cache();
        Mono<String> connectionIdMono = accessTokenMono
                .flatMap(kakaoOauthClient::getTokenInfo)
                .doOnNext(kakaoTokenInfoResponse -> log.info("Kakao Token Info = {}", kakaoTokenInfoResponse))
                .map(KakaoTokenInfoResponse::getId)
                .cache();
        return connectionIdMono
                .flatMap(connectionId -> oauthRepository.findByConnectionIdAndPlatform(connectionId, PLATFORM))
                .switchIfEmpty(Mono.defer(() -> Mono.zip(accessTokenMono, connectionIdMono)
                        .flatMap(t -> createUserWithOauth(t.getT1(), t.getT2()))))
                .map(Oauth::getUserId)
                .flatMap(userRepository::findById)
                .doOnNext(user -> log.info("User = {}", user));
    }

    @Transactional
    public Mono<Oauth> createUserWithOauth(String accessToken, String connectionId) {
        return kakaoOauthClient
                .getProfile(accessToken)
                .flatMap(kakaoProfileResponse -> {
                    KakaoProfileResponse.KakaoAccount kakaoAccount = kakaoProfileResponse.getKakaoAccount();
                    Mono<User> userMono = Mono.just(User.builder()
                                    .email(kakaoAccount.getEmail())
                                    .name(kakaoAccount.getName())
                                    .nickname(kakaoAccount.getProfile().getNickname())
                                    .roles(List.of(User.Role.COMMON))
                                    .build())
                            .flatMap(userRepository::save)
                            .cache();
                    return userMono
                            .map(User::getId)
                            .map(id -> Oauth.builder()
                                    .userId(id)
                                    .connectionId(connectionId)
                                    .platform(PLATFORM)
                                    .connectedAt(LocalDateTime.now())
                                    .build())
                            .flatMap(oauthRepository::save);
                });
    }
}
