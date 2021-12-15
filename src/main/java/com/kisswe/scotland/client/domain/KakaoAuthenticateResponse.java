package com.kisswe.scotland.client.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Data
@JsonInclude
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoAuthenticateResponse {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private Long expiresIn;
    @JsonDeserialize(using = ScopeDeserializer.class)
    @JsonProperty("scope")
    private List<String> scopes;
    private Long refreshTokenExpiresIn;

    @Component
    private static class ScopeDeserializer extends StdDeserializer<List<String>> {
        protected ScopeDeserializer() {
            super(List.class);
        }

        @Override
        public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return List.of(p.getValueAsString().split(" "));
        }
    }
}
