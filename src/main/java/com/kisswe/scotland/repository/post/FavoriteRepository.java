package com.kisswe.scotland.repository.post;

import com.kisswe.scotland.database.post.Favorite;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FavoriteRepository extends R2dbcRepository<Favorite, Long> {
}
