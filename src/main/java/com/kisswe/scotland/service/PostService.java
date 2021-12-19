package com.kisswe.scotland.service;

import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.database.post.Favorite;
import com.kisswe.scotland.repository.domain.PostWithUserAndComments;
import com.kisswe.scotland.service.domain.CreatePostDto;
import com.kisswe.scotland.service.domain.UpdatePostDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
    Flux<Post> getPosts();
    Flux<PostWithUserAndComments> getPostsWithUserAndComments();
    Flux<PostWithUserAndComments> getPostsForUser(Long userId);
    Mono<Post> getPostById(Long id);
    Mono<Post> getPostByUser(Long id, Long userId);
    Mono<Post> createPost(CreatePostDto createPostDto);
    Mono<Post> updatePost(UpdatePostDto updatePostDto);
    Mono<Favorite> markPostFavorite(Long id, Long userId);
    Mono<Void> deletePostForUser(Long id, Long userId);
    Mono<Void> deletePost(Long id);
    Mono<Void> deletePost(Post post);
}
