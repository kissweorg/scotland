package com.kisswe.scotland.service;

import com.kisswe.scotland.database.User;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

public interface RefreshTokenService {
    Mono<UUID> generateRefreshToken(User user);

    Mono<User> getUserFromRefreshToken(UUID refreshToken);

    Duration getDuration();
}
