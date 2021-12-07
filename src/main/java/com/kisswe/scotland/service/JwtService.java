package com.kisswe.scotland.service;

import com.kisswe.scotland.database.User;
import reactor.core.publisher.Mono;

public interface JwtService {
    Mono<String> createAccessToken(User user);
}
