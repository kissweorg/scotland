package com.kisswe.scotland.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kisswe.scotland.config.AuthHeaderConfig;
import com.kisswe.scotland.database.post.Comment;
import com.kisswe.scotland.service.CommentService;
import com.kisswe.scotland.service.domain.CreateCommentDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class CommentHandler extends BaseHandler {
    private final CommentService commentService;


    @Autowired
    public CommentHandler(AuthHeaderConfig authHeaderConfig, CommentService commentService) {
        super(authHeaderConfig);
        this.commentService = commentService;
    }

    public Mono<ServerResponse> createComment(ServerRequest request) {
        Long postId = Long.parseLong(request.pathVariable("postId"));
        Long userId = parseUserIdFromRequest(request);

        return request
                .bodyToMono(CreateCommentRequest.class)
                .map(createCommentRequest ->
                        CreateCommentDto.builder()
                                .postId(postId)
                                .userId(userId)
                                .content(createCommentRequest.getContent())
                                .imageUrl(createCommentRequest.getImageUrl())
                                .build())
                .flatMap(commentService::createComment)
                .map(GetCommentResponse::from)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateCommentRequest {
        private String content;
        private String imageUrl;
    }

    @Builder
    @Data
    public static class GetCommentResponse {
        private Long id;
        private Long postId;
        private Long userId;
        private String content;
        private String imageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static GetCommentResponse from(Comment comment) {
            return builder()
                    .id(comment.getId())
                    .postId(comment.getPostId())
                    .userId(comment.getUserId())
                    .content(comment.getContent())
                    .imageUrl(comment.getImageUrl())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build();
        }
    }
}
