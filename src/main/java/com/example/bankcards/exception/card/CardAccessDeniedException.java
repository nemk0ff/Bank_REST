package com.example.bankcards.exception.card;

import com.example.bankcards.exception.BankSystemException;

public class CardAccessDeniedException extends BankSystemException {
  public CardAccessDeniedException(Long cardId) {
    super("Доступ к карте " + cardId + " запрещен", "Ошибка доступа к карте");
  }
}