package com.kisswe.scotland.repository;

import com.kisswe.scotland.database.Post;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository extends R2dbcRepository<Post, Long> {
    Flux<Post> findAllByUserId(String userId);
    Mono<Post> findByIdAndUserId(Long id, String userId);
}
