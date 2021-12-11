package com.kisswe.scotland.service.domain;

import com.kisswe.scotland.database.Post;
import com.kisswe.scotland.database.User;
import com.kisswe.scotland.database.post.Comment;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PostWithUserAndCommentsDto {
    private Post post;
    private User user;
    private List<Comment> comments;
}
