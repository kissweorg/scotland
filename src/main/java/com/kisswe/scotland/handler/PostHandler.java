package com.kisswe.scotland.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kisswe.scotland.config.AuthHeaderConfig;
import com.kisswe.scotland.database.Comment;
import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.service.CommentService;
import com.kisswe.scotland.service.PostService;
import com.kisswe.scotland.service.domain.CreatePostDto;
import com.kisswe.scotland.service.domain.UpdatePostDto;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PostHandler extends BaseHandler {
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public PostHandler(AuthHeaderConfig authHeaderConfig, PostService postService, CommentService commentService) {
        super(authHeaderConfig);
        this.postService = postService;
        this.commentService = commentService;
    }

    public Mono<ServerResponse> getMyPosts(ServerRequest request) {
        Flux<IndexPostResponse> indexPostResponseFlux = postService
                .getPosts()
                .map(IndexPostResponse::from);
        return ServerResponse.ok().body(indexPostResponseFlux, IndexPostResponse.class);
    }

    public Mono<ServerResponse> getAllPosts(ServerRequest request) {
        Flux<IndexPostResponse> indexPostResponseFlux = postService
                .getPostsForUser(getUserIdFromRequest(request))
                .map(IndexPostResponse::from);
        return ServerResponse.ok().body(indexPostResponseFlux, IndexPostResponse.class);
    }

    public Mono<ServerResponse> createPost(ServerRequest request) {
        String userId = getUserIdFromRequest(request);

        return request
                .bodyToMono(CreatePostRequest.class)
                .map(createPostRequest ->
                        CreatePostDto.builder()
                                .userId(userId)
                                .content(createPostRequest.getContent())
                                .imageUrl(createPostRequest.getImageUrl())
                                .build())
                .flatMap(postService::createPost)
                .map(GetPostResponse::from)
                .doOnNext(getPostResponse -> log.info("{}", getPostResponse))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> updatePost(ServerRequest request) {
        Long postId = Long.parseLong(request.pathVariable("postId"));
        String userId = getUserIdFromRequest(request);
        return request
                .bodyToMono(UpdatePostRequest.class)
                .map(updatePostRequest ->
                        UpdatePostDto.builder()
                                .postId(postId)
                                .userId(userId)
                                .content(updatePostRequest.getContent())
                                .imageUrl(updatePostRequest.getImageUrl())
                                .build())
                .flatMap(postService::updatePost)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> deletePost(ServerRequest request) {
        Long postId = Long.parseLong(request.pathVariable("postId"));
        String userId = getUserIdFromRequest(request);

        return postService.getPostByUser(postId, userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(postService::deletePost)
                .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> getPost(ServerRequest request) {
        Long postId = Long.parseLong(request.pathVariable("postId"));
        Mono<Post> postMono = postService
                .getPostById(postId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .cache();
        Mono<List<Comment>> commentsMono = postMono.flatMapMany(commentService::getCommentsForPost).collectList();
        return Mono.zip(postMono, commentsMono)
                .map(postAndComments -> GetPostResponse.from(postAndComments.getT1(), postAndComments.getT2()))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    @Data
    @Builder
    @JsonInclude
    public static class IndexPostResponse {
        private Long id;
        private String userId;
        private String content;

        public static IndexPostResponse from(Post post) {
            return builder()
                    .id(post.getId())
                    .userId(post.getUserId())
                    .content(post.getContent())
                    .build();
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreatePostRequest {
        private String content;
        private String imageUrl;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpdatePostRequest {
        private String content;
        private String imageUrl;
    }

    @Data
    @Builder
    @JsonInclude
    public static class GetPostResponse {
        private Long id;
        private String userId;
        private String content;
        private List<CommentHandler.GetCommentResponse> comments;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static GetPostResponse from(Post post) {
            return builder()
                    .id(post.getId())
                    .userId(post.getUserId())
                    .content(post.getContent())
                    .comments(Collections.emptyList())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }

        public static GetPostResponse from(Post post, List<Comment> comments) {
            List<CommentHandler.GetCommentResponse> commentResponses =
                    comments.stream().map(CommentHandler.GetCommentResponse::from).collect(Collectors.toList());
            return builder()
                    .id(post.getId())
                    .userId(post.getUserId())
                    .content(post.getContent())
                    .comments(commentResponses)
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }
}
