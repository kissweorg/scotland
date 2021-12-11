package com.kisswe.scotland.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@Table("posts")
public class Post {
    private static final int SUMMARY_CHAR_LENGTH = 128;
    @Id
    private Long id;
    private Long userId;
    private String topic;
    private String content;
    private String imageUrl;
    private String thumbnailUrl;
    private Boolean resolved;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Version
    private Long version;

    public String getSummarizedContent() {
        if (content.length() < SUMMARY_CHAR_LENGTH)
            return content;
        return content.substring(0, SUMMARY_CHAR_LENGTH) + "...";
    }
}
