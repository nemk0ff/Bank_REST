package com.example.bankcards.dto.auth;

import com.example.bankcards.entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public record UserResponseDTO(
    Long id,

    String email,

    Role role,

    String name,

    String surname,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate birthDate,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime registeredAt
) {
}