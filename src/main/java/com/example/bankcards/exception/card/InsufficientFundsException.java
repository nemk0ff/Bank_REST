package com.example.bankcards.exception.card;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(Long cardId, BigDecimal balance, BigDecimal amount) {
    super("Недостаточно средств на карте " + cardId +
        ". Баланс: " + balance + ", требуется: " + amount);
  }
}