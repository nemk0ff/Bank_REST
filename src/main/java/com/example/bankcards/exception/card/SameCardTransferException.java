package com.example.bankcards.exception.card;

public class SameCardTransferException extends RuntimeException {
  public SameCardTransferException(Long cardId) {
    super("Нельзя переводить на ту же карту: " + cardId);
  }
}
