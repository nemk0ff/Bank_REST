package com.example.bankcards.exception.card;

public class CardNotFoundException extends RuntimeException {
  public CardNotFoundException(Long cardId) {
    super("Карта с ID " + cardId + " не найдена");
  }
}