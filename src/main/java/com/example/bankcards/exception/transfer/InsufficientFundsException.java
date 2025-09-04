package com.example.bankcards.exception.transfer;

import com.example.bankcards.exception.BankSystemException;
import java.math.BigDecimal;

public class InsufficientFundsException extends BankSystemException {
  public InsufficientFundsException(Long cardId, BigDecimal balance, BigDecimal amount) {
    super("Недостаточно средств на карте " + cardId +
        ". Баланс: " + balance + ", требуется: " + amount, "Ошибка при переводе денежных средств");
  }
}