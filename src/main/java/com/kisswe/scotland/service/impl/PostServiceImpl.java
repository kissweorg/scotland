package com.kisswe.scotland.service.impl;

import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.repository.PostRepository;
import com.kisswe.scotland.repository.domain.PostWithUserAndComments;
import com.kisswe.scotland.service.PostService;
import com.kisswe.scotland.service.domain.CreatePostDto;
import com.kisswe.scotland.service.domain.UpdatePostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public Flux<Post> getPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "created_at"));
    }

    // TODO: Write custom join SQL
    @Override
    public Flux<PostWithUserAndComments> getPostsWithUserAndComments() {
        return postRepository.findAllWithUserAndComments();
    }

    @Override
    public Flux<PostWithUserAndComments> getPostsForUser(Long userId) {
        return postRepository.findAllByUserIdWithComments(userId);
    }

    @Override
    public Mono<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Mono<Post> getPostByUser(Long id, Long userId) {
        return postRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public Mono<Post> createPost(CreatePostDto createPostDto) {
        return postRepository.save(createPostDto.toPost());
    }

    @Override
    public Mono<Post> updatePost(UpdatePostDto updatePostDto) {
        return postRepository.findByIdAndUserId(updatePostDto.getPostId(), updatePostDto.getUserId())
                .doOnNext(post -> {
                    post.setContent(updatePostDto.getContent());
                    post.setImageUrl(updatePostDto.getImageUrl());
                })
                .flatMap(postRepository::save);
    }

    @Transactional
    @Override
    public Mono<Void> deletePostForUser(Long id, Long userId) {
        return postRepository
                .findByIdAndUserId(id, userId)
                .flatMap(postRepository::delete);
    }

    @Override
    public Mono<Void> deletePost(Long id) {
        return postRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deletePost(Post post) {
        return postRepository.delete(post);
    }
}
