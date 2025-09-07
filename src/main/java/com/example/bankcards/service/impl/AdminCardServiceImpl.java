package com.example.bankcards.service.impl;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.card.CreateCardRequestDTO;
import com.example.bankcards.dto.mapper.BankCardMapper;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.auth.UserNotFoundException;
import com.example.bankcards.exception.card.CardAlreadyExistsException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AdminCardService;
import com.example.bankcards.util.EncryptionService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCardServiceImpl implements AdminCardService {

  private final BankCardRepository cardRepository;
  private final UserRepository userRepository;
  private final EncryptionService encryptionService;

  @Transactional
  public BankCardDTO createCard(CreateCardRequestDTO requestDTO) {
    log.info("Админ создает карту для пользователя ID: {}", requestDTO.userId());

    User user = userRepository.findById(requestDTO.userId())
        .orElseThrow(() -> new UserNotFoundException(requestDTO.userId().toString()));

    String encryptedCardNumber = encryptionService.encrypt(requestDTO.cardNumber());
    if (cardRepository.existsByCardNumber(encryptedCardNumber)) {
      throw new CardAlreadyExistsException(requestDTO.cardNumber());
    }

    BankCard card = BankCard.builder()
        .cardNumber(encryptedCardNumber)
        .maskedNumber(generateMaskedNumber(requestDTO.cardNumber()))
        .cardHolder(requestDTO.cardHolder().toUpperCase())
        .expiryDate(requestDTO.expiryDate())
        .status(CardStatus.ACTIVE)
        .balance(BigDecimal.ZERO) // Начальный баланс 0
        .user(user)
        .build();

    BankCard savedCard = cardRepository.save(card);
    log.info("Карта создана успешно. ID: {}", savedCard.getId());

    return BankCardMapper.INSTANCE.toCardDTO(savedCard);
  }

  @Transactional
  public BankCardDTO blockCard(Long cardId) {
    log.info("Админ блокирует карту ID: {}", cardId);
    return updateCardStatus(cardId, CardStatus.BLOCKED);
  }

  @Transactional
  public BankCardDTO activateCard(Long cardId) {
    log.info("Админ активирует карту ID: {}", cardId);
    return updateCardStatus(cardId, CardStatus.ACTIVE);
  }

  @Transactional
  public void deleteCard(Long cardId) {
    log.info("Админ удаляет карту ID: {}", cardId);

    if (!cardRepository.existsById(cardId)) {
      throw new CardNotFoundException(cardId);
    }

    cardRepository.deleteById(cardId);
    log.info("Карта ID: {} удалена", cardId);
  }

  @Transactional(readOnly = true)
  public Page<BankCardDTO> getAllCards(Pageable pageable) {
    log.debug("Админ запрашивает все карты");
    return cardRepository.findAll(pageable)
        .map(BankCardMapper.INSTANCE::toCardDTO);
  }

  @Transactional(readOnly = true)
  public BankCardDTO getCardById(Long cardId) {
    log.debug("Админ запрашивает карту ID: {}", cardId);

    BankCard card = cardRepository.findById(cardId)
        .orElseThrow(() -> new CardNotFoundException(cardId));

    return BankCardMapper.INSTANCE.toCardDTO(card);
  }

  @Transactional(readOnly = true)
  public Page<BankCardDTO> getCardsByUser(Long userId, Pageable pageable) {
    log.debug("Админ запрашивает карты пользователя ID: {}", userId);

    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId.toString());
    }

    return cardRepository.findByUserId(userId, pageable)
        .map(BankCardMapper.INSTANCE::toCardDTO);
  }

  @Transactional(readOnly = true)
  public List<BankCardDTO> getCardsByUser(Long userId) {
    log.debug("Админ запрашивает все карты пользователя ID: {}", userId);

    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId.toString());
    }

    return cardRepository.findByUserId(userId).stream()
        .map(BankCardMapper.INSTANCE::toCardDTO)
        .toList();
  }

  private BankCardDTO updateCardStatus(Long cardId, CardStatus newStatus) {
    BankCard card = cardRepository.findById(cardId)
        .orElseThrow(() -> new CardNotFoundException(cardId));

    card.setStatus(newStatus);
    BankCard updatedCard = cardRepository.save(card);

    log.info("Статус карты ID: {} изменен на {}", cardId, newStatus);
    return BankCardMapper.INSTANCE.toCardDTO(updatedCard);
  }

  private String generateMaskedNumber(String cardNumber) {
    return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
  }
}
