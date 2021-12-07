package com.kisswe.scotland.repository;

import com.kisswe.scotland.database.post.Comment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends R2dbcRepository<Comment, Long> {
    Flux<Comment> findAllByPostId(Long postId);
}
