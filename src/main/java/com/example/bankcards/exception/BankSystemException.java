package com.example.bankcards.exception;

import lombok.Getter;

@Getter
public class BankSystemException extends RuntimeException {
  private final String action;

  public BankSystemException(String message, String action) {
    super(message);
    this.action = action;
  }
}
