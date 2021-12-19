package com.kisswe.scotland.service.impl;

import com.kisswe.scotland.database.RefreshToken;
import com.kisswe.scotland.database.User;
import com.kisswe.scotland.repository.RefreshTokenRepository;
import com.kisswe.scotland.repository.UserRepository;
import com.kisswe.scotland.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${auth.refresh-token.duration}")
    private Duration duration;

    @Override
    public Mono<UUID> generateRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID())
                .userId(user.getId())
                .expiredAt(LocalDateTime.now().plus(duration))
                .build();

        return refreshTokenRepository.save(refreshToken).map(RefreshToken::getToken);
    }

    @Override
    public Mono<User> getUserFromRefreshToken(UUID refreshToken) {
        return Mono.just(refreshToken)
                .flatMap(refreshTokenRepository::findByToken)
                .doOnNext(foundRefreshToken -> log.info("Found refresh token = {}", foundRefreshToken))
                .map(RefreshToken::getUserId)
                .flatMap(userRepository::findById);
    }

    @Override
    public Mono<Void> removeRefreshToken(UUID refreshToken) {
        return Mono.just(refreshToken)
                .flatMap(refreshTokenRepository::deleteByToken);
    }

    @Override
    public Duration getDuration() {
        return duration;
    }
}
