package com.kisswe.scotland.client;

import com.kisswe.scotland.database.post.Comment;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class NotificationClient {
    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:8091")
            .filter((request, next) ->
                    next.exchange(request)
                            .onErrorResume(WebClientResponseException.class, ex -> Mono.empty()))
            .build();

    public Mono<Void> addCommentNotification(Comment comment) {
        return client
                .post()
                .uri("/push/comment/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(AddCommentNotificationRequest.from(comment))
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    @Data
    @Builder
    private static class AddCommentNotificationRequest {
        private Long id;
        private String content;

        public static AddCommentNotificationRequest from(Comment comment) {
            return AddCommentNotificationRequest.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .build();
        }
    }
}
