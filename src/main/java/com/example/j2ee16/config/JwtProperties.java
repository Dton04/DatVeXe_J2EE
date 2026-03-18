package com.example.j2ee16.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String secretBase64;

    private String secret;

    @NotBlank
    private String issuer;

    @Min(1)
    private long accessTokenTtlSeconds;

    @Min(1)
    private long refreshTokenTtlSeconds;

    @AssertTrue(message = "JWT secret is required")
    public boolean isSecretValid() {
        if (secretBase64 != null && !secretBase64.isBlank()) {
            return true;
        }
        if (secret != null && !secret.isBlank()) {
            return secret.length() >= 32;
        }
        return false;
    }

    public String getSecretBase64() {
        return secretBase64;
    }

    public void setSecretBase64(String secretBase64) {
        this.secretBase64 = secretBase64;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public long getRefreshTokenTtlSeconds() {
        return refreshTokenTtlSeconds;
    }

    public void setRefreshTokenTtlSeconds(long refreshTokenTtlSeconds) {
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }
}
