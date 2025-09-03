package com.example.bankcards.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class UserNotFoundException extends AuthenticationException {
  public UserNotFoundException(String idOrEmail) {
    super("Пользователь " + idOrEmail + " не зарегистрирован");
  }
}
