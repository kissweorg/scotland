package com.kisswe.scotland.repository;

import com.kisswe.scotland.database.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Flux<User> findAllByIdIn(Set<Long> ids);

}
