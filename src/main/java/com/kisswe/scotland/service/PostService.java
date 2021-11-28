package com.kisswe.scotland.service;

import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.service.domain.CreatePostDto;
import com.kisswe.scotland.service.domain.UpdatePostDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
    Flux<Post> getPosts();
    Flux<Post> getPostsForUser(String userId);
    Mono<Post> getPostById(Long id);
    Mono<Post> getPostByUser(Long id, String userId);
    Mono<Post> createPost(CreatePostDto createPostDto);
    Mono<Post> updatePost(UpdatePostDto updatePostDto);
    Mono<Void> deletePostForUser(Long id, String userId);
    Mono<Void> deletePost(Long id);
    Mono<Void> deletePost(Post post);
}
