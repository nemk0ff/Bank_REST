package com.example.bankcards;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface TestConstants {
  Long TEST_USER_ID = 1L;
  Long TEST_CARD_ID = 1L;

  String TEST_EMAIL = "user@example.com";
  String TEST_PASSWORD = "password123";
  String TEST_NAME = "Иван";
  String TEST_SURNAME = "Иванов";
  String TEST_JWT_TOKEN = "jwt.token.123";

  String TEST_CARD_NUMBER = "5536911234567890";
  String TEST_MASKED_NUMBER = "**** **** **** 7890";
  String TEST_CARD_HOLDER = "IVAN IVANOV";
  LocalDate TEST_EXPIRY_DATE = LocalDate.now().plusYears(3);

  BigDecimal TEST_BALANCE = new BigDecimal("1000.50");
  BigDecimal TEST_TRANSFER_AMOUNT = new BigDecimal("100.00");

  Role TEST_ROLE = Role.USER;
  CardStatus TEST_CARD_STATUS = CardStatus.ACTIVE;

  ZonedDateTime TEST_DATE = ZonedDateTime.now();
}