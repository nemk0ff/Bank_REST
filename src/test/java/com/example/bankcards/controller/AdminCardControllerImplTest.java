package com.example.bankcards.controller;

import static com.example.bankcards.TestConstants.TEST_BALANCE;
import static com.example.bankcards.TestConstants.TEST_CARD_HOLDER;
import static com.example.bankcards.TestConstants.TEST_CARD_ID;
import static com.example.bankcards.TestConstants.TEST_CARD_NUMBER;
import static com.example.bankcards.TestConstants.TEST_CARD_STATUS;
import static com.example.bankcards.TestConstants.TEST_EXPIRY_DATE;
import static com.example.bankcards.TestConstants.TEST_MASKED_NUMBER;
import static com.example.bankcards.TestConstants.TEST_USER_ID;
import com.example.bankcards.controller.card.AdminCardController;
import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.card.CreateCardRequestDTO;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.RestResponseEntityExceptionHandler;
import com.example.bankcards.service.impl.AdminCardServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminCardControllerImplTest {

  @Mock
  private AdminCardServiceImpl adminCardService;

  @InjectMocks
  private AdminCardController adminCardController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  private BankCardDTO testCard;
  private CreateCardRequestDTO testCreateRequest;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(adminCardController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .build();

    testCard = new BankCardDTO(
        TEST_CARD_ID, TEST_MASKED_NUMBER, TEST_CARD_HOLDER,
        TEST_EXPIRY_DATE, TEST_CARD_STATUS, TEST_BALANCE,
        TEST_USER_ID, LocalDateTime.now()
    );

    testCreateRequest = new CreateCardRequestDTO(
        TEST_CARD_NUMBER,
        TEST_CARD_HOLDER,
        TEST_EXPIRY_DATE,
        TEST_USER_ID
    );
  }

  @Nested
  class GetAllCardsTests {
    @Test
    void getAllCards_shouldReturnCardsPage() throws Exception {
      Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<BankCardDTO> page = new PageImpl<>(List.of(testCard), pageable, 1);

      when(adminCardService.getAllCards(any(Pageable.class)))
          .thenReturn(page);

      mockMvc.perform(get("/admin/cards")
              .param("page", "0")
              .param("size", "10")
              .param("sortBy", "createdAt")
              .param("direction", "desc"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].id").value(TEST_CARD_ID))
          .andExpect(jsonPath("$.content[0].maskedNumber").value(TEST_MASKED_NUMBER))
          .andExpect(jsonPath("$.content[0].balance").value(TEST_BALANCE.doubleValue()));
    }
  }

  @Nested
  class GetCardByIdTests {
    @Test
    void getCardById_shouldReturnCard() throws Exception {
      when(adminCardService.getCardById(TEST_CARD_ID))
          .thenReturn(testCard);

      mockMvc.perform(get("/admin/cards/{cardId}", TEST_CARD_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_CARD_ID))
          .andExpect(jsonPath("$.cardHolder").value(TEST_CARD_HOLDER));
    }
  }

  @Nested
  class CreateCardTests {
    @Test
    void createCard_shouldReturnCreatedCard() throws Exception {
      when(adminCardService.createCard(any(CreateCardRequestDTO.class)))
          .thenReturn(testCard);

      mockMvc.perform(post("/admin/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testCreateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_CARD_ID))
          .andExpect(jsonPath("$.maskedNumber").value(TEST_MASKED_NUMBER));
    }

    @Test
    void createCard_shouldReturnBadRequestForInvalidInput() throws Exception {
      CreateCardRequestDTO invalidRequest = new CreateCardRequestDTO(
          "", // invalid card number
          "", // invalid card holder
          LocalDate.now().minusDays(1), // expired date
          TEST_USER_ID
      );

      mockMvc.perform(post("/admin/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class BlockCardTests {
    @Test
    void blockCard_shouldReturnBlockedCard() throws Exception {
      BankCardDTO blockedCard = new BankCardDTO(
          TEST_CARD_ID, TEST_MASKED_NUMBER, TEST_CARD_HOLDER,
          TEST_EXPIRY_DATE, CardStatus.BLOCKED, TEST_BALANCE,
          TEST_USER_ID, LocalDateTime.now()
      );

      when(adminCardService.blockCard(TEST_CARD_ID))
          .thenReturn(blockedCard);

      mockMvc.perform(post("/admin/cards/{cardId}/block", TEST_CARD_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("BLOCKED"));
    }
  }

  @Nested
  class ActivateCardTests {
    @Test
    void activateCard_shouldReturnActivatedCard() throws Exception {
      BankCardDTO activatedCard = new BankCardDTO(
          TEST_CARD_ID, TEST_MASKED_NUMBER, TEST_CARD_HOLDER,
          TEST_EXPIRY_DATE, CardStatus.ACTIVE, TEST_BALANCE,
          TEST_USER_ID, LocalDateTime.now()
      );

      when(adminCardService.activateCard(TEST_CARD_ID))
          .thenReturn(activatedCard);

      mockMvc.perform(post("/admin/cards/{cardId}/activate", TEST_CARD_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
  }

  @Nested
  class DeleteCardTests {
    @Test
    void deleteCard_shouldReturnNoContent() throws Exception {
      mockMvc.perform(delete("/admin/cards/{cardId}", TEST_CARD_ID))
          .andExpect(status().isNoContent());
    }
  }

  @Nested
  class GetCardsByUserTests {
    @Test
    void getCardsByUser_shouldReturnCardsPage() throws Exception {
      Pageable pageable = PageRequest.of(0, 10);
      Page<BankCardDTO> page = new PageImpl<>(List.of(testCard), pageable, 1);

      when(adminCardService.getCardsByUser(eq(TEST_USER_ID), any(Pageable.class)))
          .thenReturn(page);

      mockMvc.perform(get("/admin/cards/user/{userId}", TEST_USER_ID)
              .param("page", "0")
              .param("size", "10"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].userId").value(TEST_USER_ID))
          .andExpect(jsonPath("$.content[0].id").value(TEST_CARD_ID));
    }

    @Test
    void getAllCardsByUser_shouldReturnCardsList() throws Exception {
      when(adminCardService.getCardsByUser(TEST_USER_ID))
          .thenReturn(List.of(testCard));

      mockMvc.perform(get("/admin/cards/user/{userId}/all", TEST_USER_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(TEST_CARD_ID))
          .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID));
    }
  }
}