package com.example.bankcards.dto.auth;

public record AuthResponseDTO(
    String role,
    String token
) {
}