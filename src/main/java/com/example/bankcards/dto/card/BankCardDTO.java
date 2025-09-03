package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BankCardDTO(
    Long id,

    @NotBlank(message = "Маскированный номер обязателен")
    String maskedNumber,

    @NotBlank(message = "Имя владельца обязательно")
    String cardHolder,

    @NotNull(message = "Срок действия обязателен")
    LocalDate expiryDate,

    @NotNull(message = "Статус обязателен")
    CardStatus status,

    @DecimalMin(value = "0.0", message = "Баланс не может быть отрицательным")
    BigDecimal balance,

    @NotNull(message = "ID владельца обязателен")
    Long userId, java.time.LocalDateTime createdAt) {}
