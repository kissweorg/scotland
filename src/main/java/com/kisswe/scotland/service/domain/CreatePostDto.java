package com.kisswe.scotland.service.domain;

import com.kisswe.scotland.database.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePostDto {
    private String userId;
    private String topic;
    private String content;
    private String imageUrl;

    public Post toPost() {
        return Post.builder()
                .userId(userId)
                .topic(topic)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }
}
