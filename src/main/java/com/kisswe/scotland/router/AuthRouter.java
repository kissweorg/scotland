package com.kisswe.scotland.router;

import com.kisswe.scotland.handler.AuthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthRouter {
    @Bean
    RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
        return RouterFunctions.route()
                .path("/v1/auth", builder -> builder
                        .GET("/refresh", authHandler::refreshToken)
                        .POST("/kakao", authHandler::authWithKakao))
                .build();
    }
}
