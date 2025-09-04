package com.example.bankcards.exception.card;

public class CardAlreadyBlockedException extends RuntimeException {
  public CardAlreadyBlockedException(Long cardId) {
    super("Карта " + cardId + " уже заблокирована");
  }
}