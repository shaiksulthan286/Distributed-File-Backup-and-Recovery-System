package com.dfbrs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration properties loaded from `application.yml` under the `jwt` prefix.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expirationMs) {
    public JwtProperties {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("jwt.secret must be set");
        }
        if (expirationMs <= 0) {
            throw new IllegalArgumentException("jwt.expirationMs must be positive");
        }
    }
}
