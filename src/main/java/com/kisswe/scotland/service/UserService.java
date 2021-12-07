package com.kisswe.scotland.service;

import com.kisswe.scotland.database.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> getById(Long id);
}
