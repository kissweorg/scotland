package com.kisswe.scotland.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kisswe.scotland.config.AuthHeaderConfig;
import com.kisswe.scotland.service.JwtService;
import com.kisswe.scotland.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthHandler extends BaseHandler {
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthHandler(AuthHeaderConfig authHeaderConfig, UserService userService, JwtService jwtService) {
        super(authHeaderConfig);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // TODO: Temporary solution to bypass real authentication
    public Mono<ServerResponse> authenticate(ServerRequest request) {
        return request
                .bodyToMono(AuthRequest.class)
                .map(AuthRequest::getId)
                .flatMap(userService::getById)
                .flatMap(jwtService::createAccessToken)
                .map(AuthResponse::new)
                .flatMap(authResponse -> ServerResponse.ok().bodyValue(authResponse));
    }

    @JsonInclude
    @Data
    public static class AuthRequest {
        private Long id;
    }

    @JsonInclude
    @Data
    @RequiredArgsConstructor
    public static class AuthResponse {
        private final String accessToken;
    }
}
