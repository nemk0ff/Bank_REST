package com.example.bankcards.exception.card;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.BankSystemException;

public class CardNotActiveException extends BankSystemException {
  public CardNotActiveException(Long cardId, CardStatus status) {
    super("Карта " + cardId + " не активна. Текущий статус: " + status, "Некорректный статус " +
        "карты");
  }
}