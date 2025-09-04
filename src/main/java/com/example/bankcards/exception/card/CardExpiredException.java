package com.example.bankcards.exception.card;

import com.example.bankcards.exception.BankSystemException;

public class CardExpiredException extends BankSystemException {
  public CardExpiredException(Long cardId) {
    super("Срок действия карты " + cardId + " истек", "Ошибка при действии с картой");
  }
}