package com.example.bankcards.dto.transfer;

import java.math.BigDecimal;

public record TransferResponseDTO(
    boolean success,
    String message,
    BigDecimal fromCardBalance,
    BigDecimal toCardBalance
) {
}
