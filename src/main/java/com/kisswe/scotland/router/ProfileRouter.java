package com.kisswe.scotland.router;

import com.kisswe.scotland.handler.ProfileHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class ProfileRouter {
    @Bean
    RouterFunction<ServerResponse> profileRoutes(ProfileHandler profileHandler) {
        return RouterFunctions
                .route(GET("/v1/profile"), profileHandler::getProfile);
    }
}
