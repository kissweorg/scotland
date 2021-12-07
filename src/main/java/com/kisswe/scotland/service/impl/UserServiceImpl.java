package com.kisswe.scotland.service.impl;

import com.kisswe.scotland.database.User;
import com.kisswe.scotland.repository.UserRepository;
import com.kisswe.scotland.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Mono<User> getById(Long id) {
        return userRepository.findById(id);
    }
}
