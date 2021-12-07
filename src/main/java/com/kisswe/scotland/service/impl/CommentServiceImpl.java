package com.kisswe.scotland.service.impl;

import com.kisswe.scotland.database.post.Comment;
import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.repository.CommentRepository;
import com.kisswe.scotland.service.CommentService;
import com.kisswe.scotland.service.domain.CreateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public Flux<Comment> getCommentsForPost(Post post) {
        return commentRepository.findAllByPostId(post.getId());
    }

    @Override
    public Mono<Comment> createComment(CreateCommentDto createCommentDto) {
        return commentRepository.save(createCommentDto.toComment());
    }
}
