package com.kisswe.scotland.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "auth.claim")
public class AuthClaimConfig {
    private String issuer;
    private String audience;
    private Duration duration;
}
