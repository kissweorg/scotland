package com.kisswe.scotland.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth.header")
public class AuthHeaderConfig {
    private String userId;
}
