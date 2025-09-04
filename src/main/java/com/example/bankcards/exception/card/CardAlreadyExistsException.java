package com.example.bankcards.exception.card;

import com.example.bankcards.exception.BankSystemException;

public class CardAlreadyExistsException extends BankSystemException {
  public CardAlreadyExistsException(String cardNumber) {
    super("Карта с номером " + cardNumber + " уже существует", "Ошибка при создании карты");
  }
}