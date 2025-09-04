package com.example.bankcards.exception.card;

import com.example.bankcards.entity.CardStatus;

public class CardNotActiveException extends RuntimeException {
  public CardNotActiveException(Long cardId, CardStatus status) {
    super("Карта " + cardId + " не активна. Текущий статус: " + status);
  }
}