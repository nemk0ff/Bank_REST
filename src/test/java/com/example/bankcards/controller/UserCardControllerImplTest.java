package com.example.bankcards.controller;

import static com.example.bankcards.TestConstants.TEST_BALANCE;
import static com.example.bankcards.TestConstants.TEST_CARD_HOLDER;
import static com.example.bankcards.TestConstants.TEST_CARD_ID;
import static com.example.bankcards.TestConstants.TEST_CARD_STATUS;
import static com.example.bankcards.TestConstants.TEST_EXPIRY_DATE;
import static com.example.bankcards.TestConstants.TEST_MASKED_NUMBER;
import static com.example.bankcards.TestConstants.TEST_TRANSFER_AMOUNT;
import static com.example.bankcards.TestConstants.TEST_USER_ID;
import com.example.bankcards.controller.card.UserCardController;
import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.transfer.TransferRequestDTO;
import com.example.bankcards.dto.transfer.TransferResponseDTO;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.RestResponseEntityExceptionHandler;
import com.example.bankcards.service.impl.UserCardServiceImpl;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserCardControllerImplTest {

  @Mock
  private UserCardServiceImpl userCardService;

  @InjectMocks
  private UserCardController userCardController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  private BankCardDTO testCard;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(userCardController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .build();

    testCard = new BankCardDTO(
        TEST_CARD_ID, TEST_MASKED_NUMBER, TEST_CARD_HOLDER,
        TEST_EXPIRY_DATE, TEST_CARD_STATUS, TEST_BALANCE,
        TEST_USER_ID, LocalDateTime.now()
    );

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(TEST_USER_ID, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Nested
  class GetUserCardsTests {
    @Test
    void getUserCards_shouldReturnCardsList() throws Exception {
      Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<BankCardDTO> page = new PageImpl<>(List.of(testCard), pageable, 1);

      when(userCardService.getUserCards(any(), any(Pageable.class)))
          .thenReturn(page);

      mockMvc.perform(get("/user/cards")
              .param("page", "0")
              .param("size", "10")
              .param("sortBy", "createdAt")
              .param("direction", "desc"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].id").value(TEST_CARD_ID))
          .andExpect(jsonPath("$.content[0].maskedNumber").value(TEST_MASKED_NUMBER));
    }
  }

  @Nested
  class GetCardDetailsTests {
    @Test
    void getCardDetails_shouldReturnCardDetails() throws Exception {
      when(userCardService.getCardDetails(eq(TEST_CARD_ID), any()))
          .thenReturn(testCard);

      mockMvc.perform(get("/user/cards/{cardId}", TEST_CARD_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_CARD_ID))
          .andExpect(jsonPath("$.balance").value(TEST_BALANCE.doubleValue()));
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

      when(userCardService.requestCardBlock(eq(TEST_CARD_ID), any()))
          .thenReturn(blockedCard);

      mockMvc.perform(post("/user/cards/{cardId}/block", TEST_CARD_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("BLOCKED"));
    }
  }

  @Nested
  class TransferTests {
    @Test
    void transferBetweenCards_shouldReturnSuccessResponse() throws Exception {
      TransferRequestDTO request = new TransferRequestDTO(
          TEST_CARD_ID, 2L, TEST_TRANSFER_AMOUNT
      );

      TransferResponseDTO response = new TransferResponseDTO(
          true, "Перевод выполнен успешно",
          new BigDecimal("900.50"), new BigDecimal("1100.50")
      );

      when(userCardService.transferBetweenCards(any(), any(TransferRequestDTO.class)))
          .thenReturn(response);

      mockMvc.perform(post("/user/cards/transfer")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value("Перевод выполнен успешно"));
    }
  }

  @Nested
  class GetTotalBalanceTests {
    @Test
    void getTotalBalance_shouldReturnBalance() throws Exception {
      when(userCardService.getTotalBalance(any()))
          .thenReturn(TEST_BALANCE);

      mockMvc.perform(get("/user/cards/balance/total"))
          .andExpect(status().isOk())
          .andExpect(content().string(TEST_BALANCE.toString()));
    }
  }
}