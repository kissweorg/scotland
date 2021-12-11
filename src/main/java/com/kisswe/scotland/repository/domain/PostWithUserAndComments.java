package com.kisswe.scotland.repository.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostWithUserAndComments {
    private static final int SUMMARY_CHAR_LENGTH = 128;

    private Long id;
    private String topic;
    private String content;
    private String imageUrl;
    private Boolean resolved;
    private LocalDateTime createdAt;
    private User user;
    private List<Comment> comments;

    @Data
    @Builder
    public static class User {
        private Long id;
        private String nickname;
    }

    @Data
    @Builder
    public static class Comment {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
    }

    public String getSummarizedContent() {
        if (content.length() < SUMMARY_CHAR_LENGTH)
            return content;
        return content.substring(0, SUMMARY_CHAR_LENGTH) + "...";
    }
}
