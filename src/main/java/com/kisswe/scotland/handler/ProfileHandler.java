package com.kisswe.scotland.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kisswe.scotland.config.AuthHeaderConfig;
import com.kisswe.scotland.database.User;
import com.kisswe.scotland.service.UserService;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProfileHandler extends BaseHandler {
    private final UserService userService;

    @Autowired
    public ProfileHandler(AuthHeaderConfig authHeaderConfig, UserService userService) {
        super(authHeaderConfig);
        this.userService = userService;
    }

    public Mono<ServerResponse> getProfile(ServerRequest request) {
        Long userId = parseUserIdFromRequest(request);
        return userService.getById(userId)
                .map(GetProfileResponse::from)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    @Data
    @Builder
    @JsonInclude
    public static class GetProfileResponse {
        private Long id;
        private String nickname;
        private String name;
        // TODO: ENCRYPT THIS
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static GetProfileResponse from(User user) {
            return builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .name(user.getName())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }
}
