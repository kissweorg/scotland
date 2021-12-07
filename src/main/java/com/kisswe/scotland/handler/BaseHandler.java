package com.kisswe.scotland.handler;

import com.kisswe.scotland.config.AuthHeaderConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseHandler {
    private final AuthHeaderConfig authHeaderConfig;

    protected Long parseUserIdFromRequest(ServerRequest request) {
        return Optional.ofNullable(request.headers().firstHeader(authHeaderConfig.getUserId()))
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
