package com.example.bankcards.security;

import com.example.bankcards.dto.auth.AuthRequestDTO;
import com.example.bankcards.dto.auth.AuthResponseDTO;
import com.example.bankcards.dto.auth.RegisterDTO;
import com.example.bankcards.dto.auth.UserResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
  AuthResponseDTO getAuthResponse(AuthRequestDTO requestDTO);

  UserResponseDTO register(RegisterDTO registerDTO);
}
