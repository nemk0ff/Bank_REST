package com.example.bankcards.dto.transfer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferRequestDTO(
    @NotNull(message = "ID карты отправителя обязательно")
    Long fromCardId,

    @NotNull(message = "ID карты получателя обязательно")
    Long toCardId,

    @NotNull(message = "Сумма перевода обязательна")
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше 0")
    BigDecimal amount
) {
}