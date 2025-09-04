package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.transfer.TransferRequestDTO;
import com.example.bankcards.dto.transfer.TransferResponseDTO;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.card.*;
import com.example.bankcards.exception.transfer.InsufficientFundsException;
import com.example.bankcards.exception.transfer.InvalidAmountException;
import com.example.bankcards.exception.transfer.SameCardTransferException;
import com.example.bankcards.repository.BankCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCardService {

  private final BankCardRepository cardRepository;

  @Transactional(readOnly = true)
  public Page<BankCardDTO> getUserCards(Long userId, Pageable pageable) {
    log.debug("Пользователь {} запрашивает свои карты", userId);
    return cardRepository.findByUserId(userId, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public Page<BankCardDTO> getUserActiveCards(Long userId, Pageable pageable) {
    log.debug("Пользователь {} запрашивает активные карты", userId);
    return cardRepository.findByUserIdAndStatus(userId, CardStatus.ACTIVE, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public BankCardDTO getCardDetails(Long cardId, Long userId) {
    log.debug("Пользователь {} запрашивает детали карты {}", userId, cardId);

    BankCard card = getCardWithAccessCheck(cardId, userId);
    return convertToDTO(card);
  }

  @Transactional
  public BankCardDTO requestCardBlock(Long cardId, Long userId) {
    log.info("Пользователь {} запрашивает блокировку карты {}", userId, cardId);

    BankCard card = getCardWithAccessCheck(cardId, userId);

    if (card.getStatus() == CardStatus.BLOCKED) {
      throw new CardAlreadyBlockedException(cardId);
    }

    card.setStatus(CardStatus.BLOCKED);
    BankCard updatedCard = cardRepository.save(card);

    log.info("Карта {} заблокирована пользователем {}", cardId, userId);
    return convertToDTO(updatedCard);
  }

  @Transactional
  public TransferResponseDTO transferBetweenCards(Long userId, TransferRequestDTO requestDTO) {
    log.info("Пользователь {} выполняет перевод: {} -> {}",
        userId, requestDTO.fromCardId(), requestDTO.toCardId());

    BankCard fromCard = getCardWithAccessCheck(requestDTO.fromCardId(), userId);
    BankCard toCard = getCardWithAccessCheck(requestDTO.toCardId(), userId);

    validateTransfer(fromCard, toCard, requestDTO.amount());

    // Выполняем перевод
    fromCard.setBalance(fromCard.getBalance().subtract(requestDTO.amount()));
    toCard.setBalance(toCard.getBalance().add(requestDTO.amount()));

    cardRepository.save(fromCard);
    cardRepository.save(toCard);

    log.info("Перевод успешно выполнен: {} {} с карты {} на карту {}",
        requestDTO.amount(), "RUB", fromCard.getMaskedNumber(), toCard.getMaskedNumber());

    return new TransferResponseDTO(
        true,
        "Перевод выполнен успешно",
        fromCard.getBalance(),
        toCard.getBalance()
    );
  }

  @Transactional(readOnly = true)
  public BigDecimal getTotalBalance(Long userId) {
    log.debug("Пользователь {} запрашивает общий баланс", userId);

    List<BankCard> cards = cardRepository.findByUserId(userId);
    return cards.stream()
        .filter(card -> card.getStatus() == CardStatus.ACTIVE)
        .map(BankCard::getBalance)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Transactional(readOnly = true)
  public Page<BankCardDTO> searchUserCards(Long userId, String search, Pageable pageable) {
    log.debug("Пользователь {} ищет карты по запросу: {}", userId, search);

    if (search == null || search.trim().isEmpty()) {
      return getUserCards(userId, pageable);
    }

    return cardRepository.findByUserIdAndCardHolderContainingIgnoreCase(userId, search, pageable)
        .map(this::convertToDTO);
  }

  // Приватные вспомогательные методы
  private BankCard getCardWithAccessCheck(Long cardId, Long userId) {
    BankCard card = cardRepository.findById(cardId)
        .orElseThrow(() -> new CardNotFoundException(cardId));

    if (!card.getUser().getId().equals(userId)) {
      throw new CardAccessDeniedException(cardId);
    }

    return card;
  }

  private void validateTransfer(BankCard fromCard, BankCard toCard, BigDecimal amount) {
    if (fromCard.getStatus() != CardStatus.ACTIVE) {
      throw new CardNotActiveException(fromCard.getId(), fromCard.getStatus());
    }

    if (toCard.getStatus() != CardStatus.ACTIVE) {
      throw new CardNotActiveException(toCard.getId(), toCard.getStatus());
    }

    if (fromCard.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException(fromCard.getId(), fromCard.getBalance(), amount);
    }

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidAmountException(amount);
    }

    if (fromCard.getId().equals(toCard.getId())) {
      throw new SameCardTransferException(fromCard.getId());
    }

    if (fromCard.getExpiryDate().isBefore(LocalDate.now())) {
      throw new CardExpiredException(fromCard.getId());
    }

    if (toCard.getExpiryDate().isBefore(LocalDate.now())) {
      throw new CardExpiredException(toCard.getId());
    }
  }

  private BankCardDTO convertToDTO(BankCard card) {
    return new BankCardDTO(
        card.getId(),
        card.getMaskedNumber(),
        card.getCardHolder(),
        card.getExpiryDate(),
        card.getStatus(),
        card.getBalance(),
        card.getUser().getId(),
        card.getCreatedAt()
    );
  }
}