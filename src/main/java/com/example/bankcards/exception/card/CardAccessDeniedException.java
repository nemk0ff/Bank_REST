package com.example.bankcards.exception.card;

public class CardAccessDeniedException extends RuntimeException {
  public CardAccessDeniedException(Long cardId) {
    super("Доступ к карте " + cardId + " запрещен");
  }
}