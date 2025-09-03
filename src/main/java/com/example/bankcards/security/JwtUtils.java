package com.example.bankcards.security;

import com.example.bankcards.exception.auth.JwtAuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtils {

  private SecretKey secretKey;

  @PostConstruct
  public void init() {
    String secret = "my-very-secure-key-256-bits-long-1234567890";
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    log.info("JwtUtils initialized with secret key");
  }

  public String generateToken(String username, String role) {
    log.info("Generating token for username: {}, role: {}", username, role);
    long expiration = 1000 * 60 * 60 * 24 * 7;
    return Jwts.builder()
        .subject(username)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(secretKey)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      throw new JwtAuthException("Invalid JWT token: " + e.getMessage());
    }
  }

  public String getUsernameFromToken(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}