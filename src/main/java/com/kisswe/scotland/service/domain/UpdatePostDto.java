package com.kisswe.scotland.service.domain;

import com.kisswe.scotland.database.Post;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePostDto {
    private Long postId;
    private String userId;
    private String content;
    private String imageUrl;
}
