package com.kisswe.scotland.repository;

import com.kisswe.scotland.repository.domain.PostWithUserAndComments;
import reactor.core.publisher.Flux;

public interface CustomPostRepository {
    Flux<PostWithUserAndComments> findAllWithUserAndComments();
    Flux<PostWithUserAndComments> findAllByUserIdWithComments(Long userId);
}
