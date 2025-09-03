package com.example.bankcards.dto.card;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateCardRequestDTO(
    @NotBlank(message = "Номер карты обязателен")
    @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты должен содержать 16 цифр")
    String cardNumber,

    @NotBlank(message = "Имя владельца обязательно")
    String cardHolder,

    @NotNull(message = "Срок действия обязателен")
    LocalDate expiryDate,

    @NotNull(message = "ID владельца обязателен")
    Long userId) {}