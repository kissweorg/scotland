package com.kisswe.scotland.service.impl;

import com.kisswe.scotland.client.NotificationClient;
import com.kisswe.scotland.database.post.Comment;
import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.repository.CommentRepository;
import com.kisswe.scotland.service.CommentService;
import com.kisswe.scotland.service.domain.CreateCommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final NotificationClient notificationClient;

    @Override
    public Flux<Comment> getCommentsForPost(Post post) {
        return commentRepository.findAllByPostId(post.getId());
    }

    @Override
    public Mono<Comment> createComment(CreateCommentDto createCommentDto) {
        Mono<Comment> commentMono = commentRepository.save(createCommentDto.toComment()).cache();
        return commentMono
                .flatMap(notificationClient::addCommentNotification)
                .then(commentMono);
    }
}
