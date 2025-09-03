package com.example.bankcards.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthException extends AuthenticationException {
  public JwtAuthException(String message) {
    super(message);
  }
}
