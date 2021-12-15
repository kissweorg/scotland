package com.kisswe.scotland.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kisswe.scotland.config.AuthHeaderConfig;
import com.kisswe.scotland.database.User;
import com.kisswe.scotland.service.JwtService;
import com.kisswe.scotland.service.KakaoAuthService;
import com.kisswe.scotland.service.RefreshTokenService;
import com.kisswe.scotland.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class AuthHandler extends BaseHandler {
    private final JwtService jwtService;
    private final KakaoAuthService kakaoAuthService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthHandler(AuthHeaderConfig authHeaderConfig, UserService userService, JwtService jwtService,
                       KakaoAuthService kakaoAuthService, RefreshTokenService refreshTokenService) {
        super(authHeaderConfig);
        this.jwtService = jwtService;
        this.kakaoAuthService = kakaoAuthService;
        this.refreshTokenService = refreshTokenService;
    }

    public Mono<ServerResponse> refreshToken(ServerRequest request) {
        UUID refreshToken = Optional.ofNullable(request.cookies().getFirst("rft"))
                .map(HttpCookie::getValue)
                .map(UUID::fromString)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return refreshTokenService
                .getUserFromRefreshToken(refreshToken)
                .flatMap(this::generateAuthResponseFromUserMono);
    }

    public Mono<ServerResponse> authWithKakao(ServerRequest request) {
        return request
                .bodyToMono(KakaoAuthRequest.class)
                .map(KakaoAuthRequest::getAuthCode)
                .doOnNext(authCode -> log.info("Kakao auth code = {}", authCode))
                .flatMap(kakaoAuthService::authenticate)
                .flatMap(this::generateAuthResponseFromUserMono);
    }

    private Mono<ServerResponse> generateAuthResponseFromUserMono(User user) {
        return Mono.just(user)
                .flatMap(jwtService::createAccessToken)
                .map(AuthResponse::new)
                .flatMap(authResponse -> Mono.just(user)
                        .flatMap(refreshTokenService::generateRefreshToken)
                        .flatMap(refreshToken -> ServerResponse
                                .ok()
                                .cookie(ResponseCookie
                                        .from("rft", refreshToken.toString())
                                        .path("/")
                                        .httpOnly(true)
                                        .maxAge(refreshTokenService.getDuration())
                                        .build())
                                .bodyValue(authResponse)));
    }

    // TODO: Need to investigate more on better oauth flow
//    public Mono<ServerResponse> authWithKakaoCallBack(ServerRequest request) {
//        String authCode = request.queryParam("code").orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
//        log.info("auth code = {}", authCode);
//        return ServerResponse.permanentRedirect(URI.create("kakao66e1a2ba1debe8a2f753f8a4767660a5://oauth"))
//                .build();
//    }

    @JsonInclude
    @Data
    public static class AuthRequest {
        private Long id;
    }

    @JsonInclude
    @Data
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class AuthResponse {
        private final String accessToken;
    }

    @JsonInclude
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaoAuthRequest {
        private String authCode;
    }
}
