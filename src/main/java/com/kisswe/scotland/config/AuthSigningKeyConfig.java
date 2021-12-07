package com.kisswe.scotland.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth.signing-key")
public class AuthSigningKeyConfig {
    private String type;
    private String filename;
    private String password;
    private String alias;
}
