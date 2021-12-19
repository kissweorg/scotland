package com.kisswe.scotland.repository;

import com.kisswe.scotland.database.RefreshToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken, String> {
    Mono<RefreshToken> findByToken(UUID token);
    Mono<Void> deleteByToken(UUID token);
}
