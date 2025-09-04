package com.example.bankcards.exception.card;

import com.example.bankcards.exception.BankSystemException;

public class CardNotFoundException extends BankSystemException {
  public CardNotFoundException(Long cardId) {
    super("Карта с ID " + cardId + " не найдена", "Ошибка при поиске карты");
  }
}