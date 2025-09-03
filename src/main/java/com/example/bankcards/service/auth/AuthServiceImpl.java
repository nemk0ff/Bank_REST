package com.example.bankcards.service.auth;

import com.example.bankcards.dto.auth.AuthRequestDTO;
import com.example.bankcards.dto.auth.AuthResponseDTO;
import com.example.bankcards.dto.auth.RegisterDTO;
import com.example.bankcards.dto.auth.UserResponseDTO;
import com.example.bankcards.entity.users.Role;
import com.example.bankcards.entity.users.User;
import com.example.bankcards.exception.auth.EmailAlreadyExistsException;
import com.example.bankcards.exception.auth.IllegalPasswordException;
import com.example.bankcards.exception.auth.UserNotRegisteredException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;

  @Transactional
  @Override
  public AuthResponseDTO getAuthResponse(AuthRequestDTO requestDTO) {
    log.info("Проверяем логин и пароль пользователя {}...", requestDTO.email());
    UserDetails correctDetails = loadUserByUsername(requestDTO.email());

    if (!passwordEncoder.matches(requestDTO.password(), correctDetails.getPassword())) {
      throw new IllegalPasswordException();
    }

    String role = correctDetails.getAuthorities()
        .iterator().next()
        .getAuthority();
    String token = jwtUtils.generateToken(requestDTO.email(), role);
    log.info("Токен сгенерирован успешно.");
    return new AuthResponseDTO(role, token);
  }

  @Transactional
  @Override
  public UserResponseDTO register(RegisterDTO regDTO) {
    log.info("Регистрируем нового пользователя {}...", regDTO.name());

    if (userRepository.existsByEmail(regDTO.email())) {
      throw new EmailAlreadyExistsException(regDTO.email());
    }

    User user = User.builder()
        .email(regDTO.email())
        .password(passwordEncoder.encode(regDTO.password()))
        .name(regDTO.name())
        .surname(regDTO.surname())
        .birthdate(regDTO.birthdate())
        .role(Role.USER)
        .registeredAt(ZonedDateTime.now())
        .build();
    log.info("birthdate: {}", regDTO.birthdate());
    User savedUser = userRepository.save(user);
    log.info("Пользователь {} успешно зарегистрирован.", regDTO.email());

    return toUserResponseDTO(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.debug("Поиск пользователя по email: {}", email);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotRegisteredException(email));

    log.debug("Пользователь найден: {}", user.getEmail());
    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .build();
  }

  private UserResponseDTO toUserResponseDTO(User user) {
    return new UserResponseDTO(
        user.getId(),
        user.getEmail(),
        user.getRole(),
        user.getName(),
        user.getSurname(),
        user.getBirthdate(),
        user.getRegisteredAt()
    );
  }
}