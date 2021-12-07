package com.kisswe.scotland.service.impl;

import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.database.User;
import com.kisswe.scotland.repository.PostRepository;
import com.kisswe.scotland.repository.UserRepository;
import com.kisswe.scotland.service.PostService;
import com.kisswe.scotland.service.domain.CreatePostDto;
import com.kisswe.scotland.service.domain.PostWithUserAndCommentsDto;
import com.kisswe.scotland.service.domain.UpdatePostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Flux<Post> getPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "created_at"));
    }

    @Override
    public Flux<PostWithUserAndCommentsDto> getPostsWithUserAndComments() {
        Flux<Post> postFlux = postRepository.findAll().cache();
        Mono<Map<Long, User>> users = postFlux.map(Post::getUserId)
                .collect(Collectors.toSet())
                .flatMapMany(userRepository::findAllByIdIn)
                .cache()
                .collectMap(User::getId);
        // TODO: REVERSE
        return postFlux.flatMap(post -> users
                .map(user -> user.get(post.getUserId()))
                .map(user -> PostWithUserAndCommentsDto.builder()
                    .post(post)
                    .user(user)
                    .build()))
                .sort(Comparator.comparing((PostWithUserAndCommentsDto postWithUserAndCommentsDto) ->
                        postWithUserAndCommentsDto.getPost().getCreatedAt()).reversed());
    }

    @Override
    public Flux<PostWithUserAndCommentsDto> getPostsForUser(Long userId) {
        log.info("Finding posts for user = {}", userId);
        Flux<Post> postFlux = postRepository.findAllByUserId(userId).cache();
        Mono<User> userMono = userRepository.findById(userId).cache();
        // TODO: REVERSE
        return postFlux.flatMap(post -> userMono.map(user -> PostWithUserAndCommentsDto.builder()
                .post(post)
                .user(user)
                .build()))
                .sort(Comparator.comparing((PostWithUserAndCommentsDto postWithUserAndCommentsDto) ->
                        postWithUserAndCommentsDto.getPost().getCreatedAt()).reversed());
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
