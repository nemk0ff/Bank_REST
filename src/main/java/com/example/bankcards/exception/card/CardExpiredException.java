package com.example.bankcards.exception.card;

public class CardExpiredException extends RuntimeException {
  public CardExpiredException(Long cardId) {
    super("Срок действия карты " + cardId + " истек");
  }
}