package com.kisswe.scotland.repository.impl;

import com.kisswe.scotland.repository.CustomPostRepository;
import com.kisswe.scotland.repository.domain.PostWithUserAndComments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {
    private final DatabaseClient databaseClient;

    @Override
    public Flux<PostWithUserAndComments> findAllWithUserAndComments() {
        String query = "SELECT p.id as p_id, p.content as p_content, p.image_url as p_image_url, p.created_at as p_created_at, " +
                "p.updated_at as p_updated_at, p.resolved as p_resolved, p.topic as p_topic, u.id as u_id, u.nickname as u_nickname, " +
                "c.id as c_id, c.content as c_content, c.created_at as c_created_at " +
                "FROM posts as p " +
                "LEFT OUTER JOIN users as u ON p.user_id = u.id " +
                "LEFT OUTER JOIN comments as c ON p.id = c.post_id " +
                "ORDER BY p.created_at ASC";
        return databaseClient
                .sql(query)
                .fetch()
                .all()
                .reduce(new HashMap<Long, PostWithUserAndComments>(), (acc, curr) -> {
                    Long postId = nonNullConvertTo(curr.get("p_id"), Long.class);
                    var commentCreatedAt = nonNullConvertTo(curr.get("c_created_at"), OffsetDateTime.class);
                    PostWithUserAndComments.Comment comment = PostWithUserAndComments.Comment.builder()
                            .id(nonNullConvertTo(curr.get("c_id"), Long.class))
                            .content(nonNullConvertTo(curr.get("c_content"), String.class))
                            .createdAt(commentCreatedAt != null ? commentCreatedAt.toLocalDateTime() : null)
                            .build();
                    if (comment.getId() != null && acc.containsKey(postId)) {
                        PostWithUserAndComments postWithUserAndComments = acc.get(postId);
                        List<PostWithUserAndComments.Comment> comments = new ArrayList<>(postWithUserAndComments.getComments());
                        comments.add(comment);
                        postWithUserAndComments.setComments(comments);
                        acc.put(postId, postWithUserAndComments);
                        return acc;
                    }
                    PostWithUserAndComments.User user = PostWithUserAndComments.User.builder()
                            .id(nonNullConvertTo(curr.get("u_id"), Long.class))
                            .nickname(nonNullConvertTo(curr.get("u_nickname"), String.class))
                            .build();
                    var postCreatedAt = nonNullConvertTo(curr.get("p_created_at"), OffsetDateTime.class);
                    PostWithUserAndComments post = PostWithUserAndComments.builder()
                            .id(postId)
                            .topic(nonNullConvertTo(curr.get("p_topic"), String.class))
                            .content(nonNullConvertTo(curr.get("p_content"), String.class))
                            .imageUrl(nonNullConvertTo(curr.get("p_image_url"), String.class))
                            .resolved(nonNullConvertTo(curr.get("p_resolved"), Boolean.class))
                            .createdAt(postCreatedAt != null ? postCreatedAt.toLocalDateTime() : null)
                            .user(user)
                            .comments(comment.getId() != null ? List.of(comment) : Collections.emptyList())
                            .build();
                    acc.put(postId, post);
                    return acc;
                })
                .flatMapMany(map ->
                        Flux.fromIterable(map.values()).sort(Comparator.comparing(PostWithUserAndComments::getCreatedAt).reversed()));
    }

    @Override
    public Flux<PostWithUserAndComments> findAllByUserIdWithComments(Long userId) {
        String query = "SELECT p.id as p_id, p.content as p_content, p.image_url as p_image_url, p.created_at as p_created_at, " +
                "p.updated_at as p_updated_at, p.resolved as p_resolved, p.topic as p_topic, u.id as u_id, u.nickname as u_nickname, " +
                "c.id as c_id, c.content as c_content, c.created_at as c_created_at " +
                "FROM posts as p " +
                "LEFT OUTER JOIN users as u ON p.user_id = u.id " +
                "LEFT OUTER JOIN comments as c ON p.id = c.post_id " +
                "WHERE u.id = :userId";
        return databaseClient
                .sql(query)
                .bind("userId", userId)
                .fetch()
                .all()
                .reduce(new HashMap<Long, PostWithUserAndComments>(), (acc, curr) -> {
                    Long postId = nonNullConvertTo(curr.get("p_id"), Long.class);
                    var commentCreatedAt = nonNullConvertTo(curr.get("c_created_at"), OffsetDateTime.class);
                    PostWithUserAndComments.Comment comment = PostWithUserAndComments.Comment.builder()
                            .id(nonNullConvertTo(curr.get("c_id"), Long.class))
                            .content(nonNullConvertTo(curr.get("c_content"), String.class))
                            .createdAt(commentCreatedAt != null ? commentCreatedAt.toLocalDateTime() : null)
                            .build();
                    if (comment.getId() != null && acc.containsKey(postId)) {
                        PostWithUserAndComments postWithUserAndComments = acc.get(postId);
                        List<PostWithUserAndComments.Comment> comments = new ArrayList<>(postWithUserAndComments.getComments());
                        comments.add(comment);
                        postWithUserAndComments.setComments(comments);
                        acc.put(postId, postWithUserAndComments);
                        return acc;
                    }
                    PostWithUserAndComments.User user = PostWithUserAndComments.User.builder()
                            .id(nonNullConvertTo(curr.get("u_id"), Long.class))
                            .nickname(nonNullConvertTo(curr.get("u_nickname"), String.class))
                            .build();
                    var postCreatedAt = nonNullConvertTo(curr.get("p_created_at"), OffsetDateTime.class);
                    PostWithUserAndComments post = PostWithUserAndComments.builder()
                            .id(postId)
                            .topic(nonNullConvertTo(curr.get("p_topic"), String.class))
                            .content(nonNullConvertTo(curr.get("p_content"), String.class))
                            .imageUrl(nonNullConvertTo(curr.get("p_image_url"), String.class))
                            .resolved(nonNullConvertTo(curr.get("p_resolved"), Boolean.class))
                            .createdAt(postCreatedAt != null ? postCreatedAt.toLocalDateTime() : null)
                            .user(user)
                            .comments(comment.getId() != null ? List.of(comment) : Collections.emptyList())
                            .build();
                    acc.put(postId, post);
                    return acc;
                })
                .flatMapMany(map ->
                        Flux.fromIterable(map.values()).sort(Comparator.comparing(PostWithUserAndComments::getCreatedAt).reversed()));
    }

    private static <T> T nonNullConvertTo(Object column, Class<T> T) {
        if (Objects.isNull(column))
            return null;
        return (T) column;
    }
}
