package com.kisswe.scotland.service.domain;

import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.database.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostWithUserAndCommentsDto {
    private Post post;
    private User user;
}
