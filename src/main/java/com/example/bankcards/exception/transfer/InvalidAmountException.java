package com.example.bankcards.exception.transfer;

import com.example.bankcards.exception.BankSystemException;
import java.math.BigDecimal;

public class InvalidAmountException extends BankSystemException {
  public InvalidAmountException(BigDecimal amount) {
    super("Некорректная сумма перевода: " + amount, "Ошибка при переводе денежных средств");
  }
}

