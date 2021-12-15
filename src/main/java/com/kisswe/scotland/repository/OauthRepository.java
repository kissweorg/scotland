package com.kisswe.scotland.repository;

import com.kisswe.scotland.database.Oauth;
import com.kisswe.scotland.database.Platform;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface OauthRepository extends R2dbcRepository<Oauth, Long> {
    Mono<Oauth> findByConnectionIdAndPlatform(String connectionId, Platform platform);
}
