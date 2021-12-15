package com.kisswe.scotland.client;

import com.kisswe.scotland.client.domain.KakaoAuthenticateResponse;
import com.kisswe.scotland.client.domain.KakaoProfileResponse;
import com.kisswe.scotland.client.domain.KakaoTokenInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class KakaoOauthClient {
    private final WebClient authClient;
    private final WebClient userClient;
    @Value("${auth.kakao.api-key}")
    private String apiKey;
    @Value("${auth.kakao.auth.get-token.path}")
    private String authGetTokenPath;
    @Value("${auth.kakao.user.get-token-info.path}")
    private String authGetTokenInfoPath;
    @Value("${auth.kakao.user.get-profile.path}")
    private String authGetProfilePath;


    public KakaoOauthClient(
            @Value("${auth.kakao.auth.base-url}") String authBaseUrl,
            @Value("${auth.kakao.user.base-url}") String userBaseUrl
    ) {
        authClient = WebClient.builder()
                .baseUrl(authBaseUrl)
                .build();
        userClient = WebClient.builder()
                .baseUrl(userBaseUrl)
                .build();
    }

    public Mono<KakaoAuthenticateResponse> authenticate(String authCode) {
        return authClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(authGetTokenPath)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", apiKey)
                        .queryParam("redirect_uri", "kakao66e1a2ba1debe8a2f753f8a4767660a5://oauth")
                        .queryParam("code", authCode)
                        .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .onStatus(HttpStatus::isError, (res) ->
                        Mono.error(new RuntimeException("Received error from Kakao server")))
                .bodyToMono(KakaoAuthenticateResponse.class)
                .doOnNext(body -> log.info("Response from Kakao = {}", body));
    }

    public Mono<KakaoTokenInfoResponse> getTokenInfo(String accessToken) {
        return userClient
                .get()
                .uri(authGetTokenInfoPath)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus::isError, (res) ->
                        Mono.error(new RuntimeException("Received error from kakao user server")))
                .bodyToMono(KakaoTokenInfoResponse.class)
                .doOnNext(body -> log.info("Response from Kakao user server = {}", body));
    }

    public Mono<KakaoProfileResponse> getProfile(String accessToken) {
        return userClient
                .get()
                .uri(authGetProfilePath)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus::isError, (res) ->
                        Mono.error(new RuntimeException("Received error from Kakao user profile")))
                .bodyToMono(KakaoProfileResponse.class)
                .doOnNext(body -> log.info("Profile response from Kakao user server = {}", body));
    }
}
