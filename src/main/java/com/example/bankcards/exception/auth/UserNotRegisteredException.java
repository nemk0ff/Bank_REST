package com.example.bankcards.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class UserNotRegisteredException extends AuthenticationException {
  public UserNotRegisteredException(String idOrEmail) {
    super("Пользователь " + idOrEmail + " не зарегистрирован");
  }
}
