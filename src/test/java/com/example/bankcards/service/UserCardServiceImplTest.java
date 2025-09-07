package com.example.bankcards.service;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.transfer.TransferRequestDTO;
import com.example.bankcards.dto.transfer.TransferResponseDTO;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.card.CardAccessDeniedException;
import com.example.bankcards.exception.card.CardAlreadyBlockedException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.CardNotActiveException;
import com.example.bankcards.exception.transfer.InsufficientFundsException;
import com.example.bankcards.exception.transfer.InvalidAmountException;
import com.example.bankcards.exception.transfer.SameCardTransferException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.service.impl.UserCardServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.example.bankcards.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class UserCardServiceImplTest {

  @Mock
  private BankCardRepository cardRepository;

  @InjectMocks
  private UserCardServiceImpl userCardService;

  private BankCard testCard;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID)
        .email(TEST_EMAIL)
        .name(TEST_NAME)
        .surname(TEST_SURNAME)
        .build();

    testCard = BankCard.builder()
        .id(TEST_CARD_ID)
        .maskedNumber(TEST_MASKED_NUMBER)
        .cardHolder(TEST_CARD_HOLDER)
        .expiryDate(TEST_EXPIRY_DATE)
        .status(TEST_CARD_STATUS)
        .balance(TEST_BALANCE)
        .user(testUser)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Nested
  class GetUserCardsTests {
    @Test
    void getUserCards_whenCardsExist_thenReturnPage() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<BankCard> cardPage = new PageImpl<>(List.of(testCard), pageable, 1);

      when(cardRepository.findByUserId(TEST_USER_ID, pageable)).thenReturn(cardPage);

      Page<BankCardDTO> result = userCardService.getUserCards(TEST_USER_ID, pageable);

      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).id()).isEqualTo(TEST_CARD_ID);
      verify(cardRepository).findByUserId(TEST_USER_ID, pageable);
    }
  }

  @Nested
  class GetUserActiveCardsTests {
    @Test
    void getUserActiveCards_whenActiveCardsExist_thenReturnPage() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<BankCard> cardPage = new PageImpl<>(List.of(testCard), pageable, 1);

      when(cardRepository.findByUserIdAndStatus(TEST_USER_ID, CardStatus.ACTIVE, pageable))
          .thenReturn(cardPage);

      Page<BankCardDTO> result = userCardService.getUserActiveCards(TEST_USER_ID, pageable);

      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).status()).isEqualTo(CardStatus.ACTIVE);
      verify(cardRepository).findByUserIdAndStatus(TEST_USER_ID, CardStatus.ACTIVE, pageable);
    }
  }

  @Nested
  class GetCardDetailsTests {
    @Test
    void getCardDetails_whenCardExistsAndUserHasAccess_thenReturnCard() {
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCard));

      BankCardDTO result = userCardService.getCardDetails(TEST_CARD_ID, TEST_USER_ID);

      assertThat(result.id()).isEqualTo(TEST_CARD_ID);
      assertThat(result.userId()).isEqualTo(TEST_USER_ID);
      verify(cardRepository).findById(TEST_CARD_ID);
    }

    @Test
    void getCardDetails_whenCardNotExists_thenThrowException() {
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userCardService.getCardDetails(TEST_CARD_ID, TEST_USER_ID))
          .isInstanceOf(CardNotFoundException.class)
          .hasMessageContaining("Карта с ID " + TEST_CARD_ID + " не найдена");
    }

    @Test
    void getCardDetails_whenUserHasNoAccess_thenThrowException() {
      Long otherUserId = 999L;
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCard));

      assertThatThrownBy(() -> userCardService.getCardDetails(TEST_CARD_ID, otherUserId))
          .isInstanceOf(CardAccessDeniedException.class)
          .hasMessageContaining("Доступ к карте " + TEST_CARD_ID + " запрещен");
    }
  }

  @Nested
  class RequestCardBlockTests {
    @Test
    void requestCardBlock_whenCardActive_thenReturnBlockedCard() {
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCard));
      when(cardRepository.save(any(BankCard.class))).thenReturn(testCard);

      BankCardDTO result = userCardService.requestCardBlock(TEST_CARD_ID, TEST_USER_ID);

      assertThat(result.status()).isEqualTo(CardStatus.BLOCKED);
      verify(cardRepository).save(testCard);
    }

    @Test
    void requestCardBlock_whenCardAlreadyBlocked_thenThrowException() {
      testCard.setStatus(CardStatus.BLOCKED);
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCard));

      assertThatThrownBy(() -> userCardService.requestCardBlock(TEST_CARD_ID, TEST_USER_ID))
          .isInstanceOf(CardAlreadyBlockedException.class)
          .hasMessageContaining("Карта " + TEST_CARD_ID + " уже заблокирована");
    }
  }

  @Nested
  class TransferBetweenCardsTests {
    private BankCard fromCard;
    private BankCard toCard;
    private TransferRequestDTO transferRequest;

    @BeforeEach
    void setUpTransfer() {
      fromCard = BankCard.builder()
          .id(1L)
          .maskedNumber("**** **** **** 1234")
          .cardHolder("FROM USER")
          .expiryDate(LocalDate.now().plusYears(2))
          .status(CardStatus.ACTIVE)
          .balance(new BigDecimal("1000.00"))
          .user(testUser)
          .build();

      toCard = BankCard.builder()
          .id(2L)
          .maskedNumber("**** **** **** 5678")
          .cardHolder("TO USER")
          .expiryDate(LocalDate.now().plusYears(3))
          .status(CardStatus.ACTIVE)
          .balance(new BigDecimal("500.00"))
          .user(testUser)
          .build();

      transferRequest = new TransferRequestDTO(1L, 2L, new BigDecimal("100.00"));
    }

    @Test
    void transferBetweenCards_whenValidTransfer_thenReturnSuccessResponse() {
      when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
      when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
      when(cardRepository.save(any(BankCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

      TransferResponseDTO result = userCardService.transferBetweenCards(TEST_USER_ID, transferRequest);

      assertThat(result.success()).isTrue();
      assertThat(result.message()).isEqualTo("Перевод выполнен успешно");
      assertThat(result.fromCardBalance()).isEqualByComparingTo("900.00");
      assertThat(result.toCardBalance()).isEqualByComparingTo("600.00");
      verify(cardRepository).save(fromCard);
      verify(cardRepository).save(toCard);
    }

    @Test
    void transferBetweenCards_whenInsufficientFunds_thenThrowException() {
      fromCard.setBalance(new BigDecimal("50.00"));
      when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
      when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

      assertThatThrownBy(() -> userCardService.transferBetweenCards(TEST_USER_ID, transferRequest))
          .isInstanceOf(InsufficientFundsException.class)
          .hasMessageContaining("Недостаточно средств");
    }

    @Test
    void transferBetweenCards_whenSameCard_thenThrowException() {
      TransferRequestDTO sameCardRequest = new TransferRequestDTO(1L, 1L, new BigDecimal("100.00"));
      when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));

      assertThatThrownBy(() -> userCardService.transferBetweenCards(TEST_USER_ID, sameCardRequest))
          .isInstanceOf(SameCardTransferException.class)
          .hasMessageContaining("Нельзя переводить на ту же карту");
    }

    @Test
    void transferBetweenCards_whenInvalidAmount_thenThrowException() {
      TransferRequestDTO invalidAmountRequest = new TransferRequestDTO(1L, 2L, new BigDecimal("-100.00"));
      when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
      when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

      assertThatThrownBy(() -> userCardService.transferBetweenCards(TEST_USER_ID, invalidAmountRequest))
          .isInstanceOf(InvalidAmountException.class)
          .hasMessageContaining("Некорректная сумма перевода");
    }

    @Test
    void transferBetweenCards_whenCardNotActive_thenThrowException() {
      fromCard.setStatus(CardStatus.BLOCKED);
      when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
      when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

      assertThatThrownBy(() -> userCardService.transferBetweenCards(TEST_USER_ID, transferRequest))
          .isInstanceOf(CardNotActiveException.class)
          .hasMessageContaining("Карта " + fromCard.getId() + " не активна");
    }
  }

  @Nested
  class GetTotalBalanceTests {
    @Test
    void getTotalBalance_whenActiveCardsExist_thenReturnSum() {
      BankCard card1 = BankCard.builder()
          .status(CardStatus.ACTIVE)
          .balance(new BigDecimal("1000.00"))
          .user(testUser)
          .build();

      BankCard card2 = BankCard.builder()
          .status(CardStatus.ACTIVE)
          .balance(new BigDecimal("500.50"))
          .user(testUser)
          .build();

      BankCard blockedCard = BankCard.builder()
          .status(CardStatus.BLOCKED)
          .balance(new BigDecimal("200.00"))
          .user(testUser)
          .build();

      when(cardRepository.findByUserId(TEST_USER_ID)).thenReturn(List.of(card1, card2, blockedCard));

      BigDecimal result = userCardService.getTotalBalance(TEST_USER_ID);

      assertThat(result).isEqualByComparingTo("1500.50");
      verify(cardRepository).findByUserId(TEST_USER_ID);
    }

    @Test
    void getTotalBalance_whenNoActiveCards_thenReturnZero() {
      BankCard blockedCard = BankCard.builder()
          .status(CardStatus.BLOCKED)
          .balance(new BigDecimal("200.00"))
          .user(testUser)
          .build();

      when(cardRepository.findByUserId(TEST_USER_ID)).thenReturn(List.of(blockedCard));

      BigDecimal result = userCardService.getTotalBalance(TEST_USER_ID);

      assertThat(result).isEqualByComparingTo("0.00");
    }
  }

  @Nested
  class SearchUserCardsTests {
    @Test
    void searchUserCards_whenSearchQueryProvided_thenReturnFilteredCards() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<BankCard> cardPage = new PageImpl<>(List.of(testCard), pageable, 1);
      String searchQuery = "IVAN";

      when(cardRepository.findByUserIdAndCardHolderContainingIgnoreCase(TEST_USER_ID, searchQuery, pageable))
          .thenReturn(cardPage);

      Page<BankCardDTO> result = userCardService.searchUserCards(TEST_USER_ID, searchQuery, pageable);

      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).cardHolder()).contains("IVAN");
      verify(cardRepository).findByUserIdAndCardHolderContainingIgnoreCase(TEST_USER_ID, searchQuery, pageable);
    }

    @Test
    void searchUserCards_whenEmptySearchQuery_thenReturnAllCards() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<BankCard> cardPage = new PageImpl<>(List.of(testCard), pageable, 1);

      when(cardRepository.findByUserId(TEST_USER_ID, pageable)).thenReturn(cardPage);

      Page<BankCardDTO> result = userCardService.searchUserCards(TEST_USER_ID, "", pageable);

      assertThat(result.getContent()).hasSize(1);
      verify(cardRepository).findByUserId(TEST_USER_ID, pageable);
    }
  }
}