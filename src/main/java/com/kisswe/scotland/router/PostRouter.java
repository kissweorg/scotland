package com.kisswe.scotland.router;

import com.kisswe.scotland.handler.CommentHandler;
import com.kisswe.scotland.handler.PostHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PostRouter {
    @Bean
    RouterFunction<ServerResponse> postRoutes(PostHandler postHandler) {
        return RouterFunctions
                .route(GET("/v1/profile/posts"), postHandler::getMyPosts)
                .andRoute(GET("/v1/posts"), postHandler::getAllPosts)
                .andRoute(POST("/v1/posts"), postHandler::createPost)
                .andRoute(GET("/v1/posts/{postId}"), postHandler::getPost)
                .andRoute(PUT("/v1/posts/{postId}"), postHandler::updatePost)
                .andRoute(POST("/v1/posts/{postId}/favorite"), postHandler::markPostFavorite)
                .andRoute(DELETE("/v1/posts/{postId}"), postHandler::deletePost);
    }

    @Bean
    RouterFunction<ServerResponse> commentRoutes(CommentHandler commentHandler) {
        return RouterFunctions
                .route(POST("/v1/posts/{postId}/comments"), commentHandler::createComment);
    }
}
