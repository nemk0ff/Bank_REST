package com.example.bankcards.exception.transfer;

import com.example.bankcards.exception.BankSystemException;

public class SameCardTransferException extends BankSystemException {
  public SameCardTransferException(Long cardId) {
    super("Нельзя переводить на ту же карту: " + cardId, "Ошибка при переводе денежных средств");
  }
}
