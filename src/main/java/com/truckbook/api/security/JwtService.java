package com.truckbook.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final Key key;
  private final Duration expiry;

  public JwtService(
      @Value("${app.jwt.secret:dev-secret-change-me-please-32chars-min}") String secret,
      @Value("${app.jwt.expiry-days:7}") long expiryDays) {
    this.key = Keys.hmacShaKeyFor(normalizeSecret(secret).getBytes(StandardCharsets.UTF_8));
    this.expiry = Duration.ofDays(expiryDays);
  }

  public String generateToken(UUID userId, UUID orgId, String phoneE164) {
    OffsetDateTime now = OffsetDateTime.now();
    Date issuedAt = Date.from(now.toInstant());
    Date expiresAt = Date.from(now.plus(expiry).toInstant());

    return Jwts.builder()
        .setSubject(userId.toString())
        .addClaims(Map.of(
            "org_id", orgId.toString(),
            "phone", phoneE164))
        .setIssuedAt(issuedAt)
        .setExpiration(expiresAt)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private static String normalizeSecret(String secret) {
    if (secret == null) {
      throw new IllegalArgumentException("JWT secret is required");
    }
    if (secret.length() >= 32) {
      return secret;
    }
    StringBuilder builder = new StringBuilder(secret);
    while (builder.length() < 32) {
      builder.append("0");
    }
    return builder.toString();
  }
}
