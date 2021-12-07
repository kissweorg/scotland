package com.kisswe.scotland.service;

import com.kisswe.scotland.database.post.Comment;
import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.service.domain.CreateCommentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService {
    Flux<Comment> getCommentsForPost(Post post);
    Mono<Comment> createComment(CreateCommentDto createCommentDto);
}
