package com.example.bankcards.controller.auth;

import com.example.bankcards.dto.auth.AuthRequestDTO;
import com.example.bankcards.dto.auth.AuthResponseDTO;
import com.example.bankcards.dto.auth.RegisterDTO;
import com.example.bankcards.dto.auth.UserResponseDTO;
import com.example.bankcards.security.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  @Override
  public ResponseEntity<?> login(@RequestBody @Valid AuthRequestDTO request) {
    log.info("Попытка входа пользователя с email: {}...", request.email());
    AuthResponseDTO response = authService.getAuthResponse(request);
    log.info("Успешный вход пользователя с email: {}.", request.email());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  @Override
  public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO registerDTO) {
    log.info("Попытка регистрации нового пользователя с email: {}...", registerDTO.email());
    UserResponseDTO response = authService.register(registerDTO);
    log.info("Успешная регистрация пользователя с email: {}", registerDTO.email());
    return ResponseEntity.ok(response);
  }
}