package com.example.bankcards.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record RegisterDTO(
    @NotBlank(message = "email не должен быть пустым")
    String email,

    @NotBlank(message = "пароль не должен быть пустым")
    String password,

    @NotBlank(message = "введите ваше имя")
    String name,

    @NotBlank(message = "введите вашу фамилию")
    String surname,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate birthdate
) {
}