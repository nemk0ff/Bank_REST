package com.example.bankcards.exception.card;

import java.math.BigDecimal;

public class InvalidAmountException extends RuntimeException {
  public InvalidAmountException(BigDecimal amount) {
    super("Некорректная сумма перевода: " + amount);
  }
}

