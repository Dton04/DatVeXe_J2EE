package com.example.j2ee16.security;

import com.example.j2ee16.config.JwtProperties;
import com.example.j2ee16.constants.JwtClaimConstants;
import com.example.j2ee16.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(resolveKeyBytes(jwtProperties));
    }

    public String generateAccessToken(User user) {
        return generateToken(user, JwtTokenType.ACCESS, jwtProperties.getAccessTokenTtlSeconds());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, JwtTokenType.REFRESH, jwtProperties.getRefreshTokenTtlSeconds());
    }

    public String extractUsername(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    public boolean isTokenType(String token, JwtTokenType expectedType) {
        Object tokenType = parseClaims(token).getPayload().get(JwtClaimConstants.TOKEN_TYPE);
        if (tokenType == null) {
            return false;
        }
        return expectedType.name().equals(tokenType.toString());
    }

    public void validate(String token) throws JwtException {
        parseClaims(token);
    }

    private String generateToken(User user, JwtTokenType tokenType, long ttlSeconds) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(JwtClaimConstants.TOKEN_TYPE, tokenType.name())
                .claim(JwtClaimConstants.ROLE, user.getRole().name())
                .signWith(secretKey)
                .compact();
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token);
    }

    private byte[] resolveKeyBytes(JwtProperties jwtProperties) {
        String secretBase64 = jwtProperties.getSecretBase64();
        if (secretBase64 != null && !secretBase64.isBlank()) {
            byte[] decoded;
            try {
                decoded = Decoders.BASE64.decode(secretBase64);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Invalid JWT_SECRET_BASE64", exception);
            }
            if (decoded.length < 32) {
                throw new IllegalArgumentException("JWT_SECRET_BASE64 must decode to at least 32 bytes");
            }
            return decoded;
        }

        String secret = jwtProperties.getSecret();
        if (secret != null && !secret.isBlank()) {
            if (secret.length() < 32) {
                throw new IllegalArgumentException("JWT_SECRET must be at least 32 characters");
            }
            return secret.getBytes(StandardCharsets.UTF_8);
        }

        throw new IllegalArgumentException("JWT secret is required");
    }
}
