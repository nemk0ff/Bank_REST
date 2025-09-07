package com.example.bankcards.service;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.card.CreateCardRequestDTO;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.auth.UserNotFoundException;
import com.example.bankcards.exception.card.CardAlreadyExistsException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.AdminCardServiceImpl;
import com.example.bankcards.util.EncryptionService;
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
class AdminCardServiceImplTest {

  @Mock
  private BankCardRepository cardRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private EncryptionService encryptionService;

  @InjectMocks
  private AdminCardServiceImpl adminCardService;

  private BankCard testCard;
  private User testUser;
  private CreateCardRequestDTO testCreateRequest;

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
        .status(CardStatus.ACTIVE)
        .balance(BigDecimal.ZERO)
        .user(testUser)
        .createdAt(LocalDateTime.now())
        .build();

    testCreateRequest = new CreateCardRequestDTO(
        TEST_CARD_NUMBER,
        TEST_CARD_HOLDER,
        TEST_EXPIRY_DATE,
        TEST_USER_ID
    );
  }

  @Nested
  class CreateCardTests {
    @Test
    void createCard_whenValidRequest_thenReturnCreatedCard() {
      String encryptedCardNumber = "encrypted_" + TEST_CARD_NUMBER;

      when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
      when(encryptionService.encrypt(TEST_CARD_NUMBER)).thenReturn(encryptedCardNumber);
      when(cardRepository.existsByCardNumber(encryptedCardNumber)).thenReturn(false);
      when(cardRepository.save(any(BankCard.class))).thenReturn(testCard);

      BankCardDTO result = adminCardService.createCard(testCreateRequest);

      assertThat(result.id()).isEqualTo(TEST_CARD_ID);
      assertThat(result.userId()).isEqualTo(TEST_USER_ID);
      assertThat(result.balance()).isEqualByComparingTo(BigDecimal.ZERO);
      verify(userRepository).findById(TEST_USER_ID);
      verify(encryptionService).encrypt(TEST_CARD_NUMBER);
      verify(cardRepository).existsByCardNumber(encryptedCardNumber);
      verify(cardRepository).save(any(BankCard.class));
    }

    @Test
    void createCard_whenUserNotFound_thenThrowException() {
      when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> adminCardService.createCard(testCreateRequest))
          .isInstanceOf(UserNotFoundException.class)
          .hasMessageContaining("Пользователь " + TEST_USER_ID + " не зарегистрирован");
    }

    @Test
    void createCard_whenCardAlreadyExists_thenThrowException() {
      String encryptedCardNumber = "encrypted_" + TEST_CARD_NUMBER;

      when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
      when(encryptionService.encrypt(TEST_CARD_NUMBER)).thenReturn(encryptedCardNumber);
      when(cardRepository.existsByCardNumber(encryptedCardNumber)).thenReturn(true);

      assertThatThrownBy(() -> adminCardService.createCard(testCreateRequest))
          .isInstanceOf(CardAlreadyExistsException.class)
          .hasMessageContaining("Карта с номером " + TEST_CARD_NUMBER + " уже существует");
    }
  }

  @Nested
  class BlockCardTests {
    @Test
    void blockCard_whenCardExists_thenReturnBlockedCard() {
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCard));
      when(cardRepository.save(any(BankCard.class))).thenReturn(testCard);

      BankCardDTO result = adminCardService.blockCard(TEST_CARD_ID);

      assertThat(result.status()).isEqualTo(CardStatus.BLOCKED);
      verify(cardRepository).findById(TEST_CARD_ID);
      verify(cardRepository).save(testCard);
    }

    @Test
    void blockCard_whenCardNotExists_thenThrowException() {
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> adminCardService.blockCard(TEST_CARD_ID))
          .isInstanceOf(CardNotFoundException.class)
          .hasMessageContaining("Карта с ID " + TEST_CARD_ID + " не найдена");
    }
  }

  @Nested
  class ActivateCardTests {
    @Test
    void activateCard_whenCardExists_thenReturnActivatedCard() {
      testCard.setStatus(CardStatus.BLOCKED);
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCard));
      when(cardRepository.save(any(BankCard.class))).thenReturn(testCard);

      BankCardDTO result = adminCardService.activateCard(TEST_CARD_ID);

      assertThat(result.status()).isEqualTo(CardStatus.ACTIVE);
      verify(cardRepository).findById(TEST_CARD_ID);
      verify(cardRepository).save(testCard);
    }

    @Test
    void activateCard_whenCardNotExists_thenThrowException() {
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> adminCardService.activateCard(TEST_CARD_ID))
          .isInstanceOf(CardNotFoundException.class)
          .hasMessageContaining("Карта с ID " + TEST_CARD_ID + " не найдена");
    }
  }

  @Nested
  class DeleteCardTests {
    @Test
    void deleteCard_whenCardExists_thenDeleteCard() {
      when(cardRepository.existsById(TEST_CARD_ID)).thenReturn(true);

      adminCardService.deleteCard(TEST_CARD_ID);

      verify(cardRepository).existsById(TEST_CARD_ID);
      verify(cardRepository).deleteById(TEST_CARD_ID);
    }

    @Test
    void deleteCard_whenCardNotExists_thenThrowException() {
      when(cardRepository.existsById(TEST_CARD_ID)).thenReturn(false);

      assertThatThrownBy(() -> adminCardService.deleteCard(TEST_CARD_ID))
          .isInstanceOf(CardNotFoundException.class)
          .hasMessageContaining("Карта с ID " + TEST_CARD_ID + " не найдена");
    }
  }

  @Nested
  class GetAllCardsTests {
    @Test
    void getAllCards_whenCardsExist_thenReturnPage() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<BankCard> cardPage = new PageImpl<>(List.of(testCard), pageable, 1);

      when(cardRepository.findAll(pageable)).thenReturn(cardPage);

      Page<BankCardDTO> result = adminCardService.getAllCards(pageable);

      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).id()).isEqualTo(TEST_CARD_ID);
      verify(cardRepository).findAll(pageable);
    }
  }

  @Nested
  class GetCardByIdTests {
    @Test
    void getCardById_whenCardExists_thenReturnCard() {
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCard));

      BankCardDTO result = adminCardService.getCardById(TEST_CARD_ID);

      assertThat(result.id()).isEqualTo(TEST_CARD_ID);
      verify(cardRepository).findById(TEST_CARD_ID);
    }

    @Test
    void getCardById_whenCardNotExists_thenThrowException() {
      when(cardRepository.findById(TEST_CARD_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> adminCardService.getCardById(TEST_CARD_ID))
          .isInstanceOf(CardNotFoundException.class)
          .hasMessageContaining("Карта с ID " + TEST_CARD_ID + " не найдена");
    }
  }

  @Nested
  class GetCardsByUserTests {
    @Test
    void getCardsByUser_whenUserExists_thenReturnPage() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<BankCard> cardPage = new PageImpl<>(List.of(testCard), pageable, 1);

      when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
      when(cardRepository.findByUserId(TEST_USER_ID, pageable)).thenReturn(cardPage);

      Page<BankCardDTO> result = adminCardService.getCardsByUser(TEST_USER_ID, pageable);

      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).userId()).isEqualTo(TEST_USER_ID);
      verify(userRepository).existsById(TEST_USER_ID);
      verify(cardRepository).findByUserId(TEST_USER_ID, pageable);
    }

    @Test
    void getCardsByUser_whenUserNotExists_thenThrowException() {
      Pageable pageable = PageRequest.of(0, 10);

      when(userRepository.existsById(TEST_USER_ID)).thenReturn(false);

      assertThatThrownBy(() -> adminCardService.getCardsByUser(TEST_USER_ID, pageable))
          .isInstanceOf(UserNotFoundException.class)
          .hasMessageContaining("Пользователь " + TEST_USER_ID + " не зарегистрирован");
    }

    @Test
    void getCardsByUserList_whenUserExists_thenReturnList() {
      when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
      when(cardRepository.findByUserId(TEST_USER_ID)).thenReturn(List.of(testCard));

      List<BankCardDTO> result = adminCardService.getCardsByUser(TEST_USER_ID);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).userId()).isEqualTo(TEST_USER_ID);
      verify(userRepository).existsById(TEST_USER_ID);
      verify(cardRepository).findByUserId(TEST_USER_ID);
    }

    @Test
    void getCardsByUserList_whenUserNotExists_thenThrowException() {
      when(userRepository.existsById(TEST_USER_ID)).thenReturn(false);

      assertThatThrownBy(() -> adminCardService.getCardsByUser(TEST_USER_ID))
          .isInstanceOf(UserNotFoundException.class)
          .hasMessageContaining("Пользователь " + TEST_USER_ID + " не зарегистрирован");
    }
  }
}