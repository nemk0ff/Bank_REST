package com.example.bankcards.exception.auth;

import com.example.bankcards.exception.BankSystemException;
import lombok.Getter;

@Getter
public class UserException extends BankSystemException {

  public UserException(String message, String action) {
    super(message, action);
  }
}
