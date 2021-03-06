package com.kisswe.scotland.service.domain;

import com.kisswe.scotland.database.post.Comment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentDto {
    private Long postId;
    private Long userId;
    private String content;
    private String imageUrl;

    public Comment toComment() {
        return Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }
}
