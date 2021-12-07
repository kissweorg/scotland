package com.kisswe.scotland.service.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePostDto {
    private Long postId;
    private Long userId;
    private String content;
    private String imageUrl;
}
