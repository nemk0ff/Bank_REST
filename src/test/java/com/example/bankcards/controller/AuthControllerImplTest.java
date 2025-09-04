package com.example.bankcards.controller;

import static com.example.bankcards.TestConstants.TEST_EMAIL;
import static com.example.bankcards.TestConstants.TEST_JWT_TOKEN;
import static com.example.bankcards.TestConstants.TEST_NAME;
import static com.example.bankcards.TestConstants.TEST_PASSWORD;
import static com.example.bankcards.TestConstants.TEST_ROLE;
import static com.example.bankcards.TestConstants.TEST_SURNAME;
import static com.example.bankcards.TestConstants.TEST_USER_ID;
import com.example.bankcards.controller.auth.AuthControllerImpl;
import com.example.bankcards.dto.auth.AuthRequestDTO;
import com.example.bankcards.dto.auth.AuthResponseDTO;
import com.example.bankcards.dto.auth.RegisterDTO;
import com.example.bankcards.dto.auth.UserResponseDTO;
import com.example.bankcards.exception.RestResponseEntityExceptionHandler;
import com.example.bankcards.exception.auth.EmailAlreadyExistsException;
import com.example.bankcards.security.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerImplTest {

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthControllerImpl authController;

  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    objectMapper.registerModule(new JavaTimeModule());
    mockMvc = MockMvcBuilders.standaloneSetup(authController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .build();
  }

  @Nested
  class LoginTests {
    @Test
    void login_shouldReturnTokenAndRole_whenCredentialsValid() throws Exception {
      AuthRequestDTO request = new AuthRequestDTO(TEST_EMAIL, TEST_PASSWORD);
      AuthResponseDTO response = new AuthResponseDTO(TEST_ROLE.name(), TEST_JWT_TOKEN);

      when(authService.getAuthResponse(request)).thenReturn(response);

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value(TEST_JWT_TOKEN))
          .andExpect(jsonPath("$.role").value(TEST_ROLE.name()));
    }

    @Test
    void login_shouldReturnBadRequest_whenEmailInvalid() throws Exception {
      AuthRequestDTO invalidRequest = new AuthRequestDTO("invalid-email", TEST_PASSWORD);

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class RegisterTests {
    @Test
    void register_shouldReturnUserResponse_whenRegistrationSuccessful() throws Exception {
      RegisterDTO request = new RegisterDTO(
          TEST_EMAIL, TEST_PASSWORD, TEST_NAME, TEST_SURNAME, LocalDate.of(1990, 1, 1)
      );

      UserResponseDTO response = new UserResponseDTO(
          TEST_USER_ID, TEST_EMAIL, TEST_ROLE, TEST_NAME, TEST_SURNAME,
          LocalDate.of(1990, 1, 1), ZonedDateTime.now()
      );

      when(authService.register(request)).thenReturn(response);

      mockMvc.perform(post("/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_USER_ID))
          .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void register_shouldHandleEmailAlreadyExists() throws Exception {
      RegisterDTO request = new RegisterDTO(
          TEST_EMAIL, TEST_PASSWORD, TEST_NAME, TEST_SURNAME, LocalDate.of(1990, 1, 1)
      );

      when(authService.register(request))
          .thenThrow(new EmailAlreadyExistsException(TEST_EMAIL));

      mockMvc.perform(post("/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.title").value("Email уже существует"))
          .andExpect(jsonPath("$.detail").value(TEST_EMAIL + " уже используется"));
    }
  }
}