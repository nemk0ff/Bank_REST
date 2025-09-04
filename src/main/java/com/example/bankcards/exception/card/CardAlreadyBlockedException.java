package com.example.bankcards.exception.card;

import com.example.bankcards.exception.BankSystemException;

public class CardAlreadyBlockedException extends BankSystemException {
  public CardAlreadyBlockedException(Long cardId) {
    super("Карта " + cardId + " уже заблокирована", "Ошибка при попытке заблокировать карту");
  }
}