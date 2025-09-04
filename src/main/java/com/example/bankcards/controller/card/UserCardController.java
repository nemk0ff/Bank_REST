package com.example.bankcards.controller.card;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.transfer.TransferRequestDTO;
import com.example.bankcards.dto.transfer.TransferResponseDTO;
import com.example.bankcards.service.impl.UserCardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/user/cards")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "User Card Operations", description = "Операции с картами для пользователей")
public class UserCardController {

  private final UserCardServiceImpl userCardServiceImpl;

  @GetMapping
  @Operation(summary = "Получить карты пользователя с пагинацией")
  public ResponseEntity<Page<BankCardDTO>> getUserCards(
      @AuthenticationPrincipal Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {

    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<BankCardDTO> cards = userCardServiceImpl.getUserCards(userId, pageable);
    return ResponseEntity.ok(cards);
  }

  @GetMapping("/active")
  @Operation(summary = "Получить активные карты пользователя")
  public ResponseEntity<Page<BankCardDTO>> getActiveCards(
      @AuthenticationPrincipal Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<BankCardDTO> cards = userCardServiceImpl.getUserActiveCards(userId, pageable);
    return ResponseEntity.ok(cards);
  }

  @GetMapping("/{cardId}")
  @Operation(summary = "Получить детали карты")
  public ResponseEntity<BankCardDTO> getCardDetails(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long cardId) {

    BankCardDTO card = userCardServiceImpl.getCardDetails(cardId, userId);
    return ResponseEntity.ok(card);
  }

  @PostMapping("/{cardId}/block")
  @Operation(summary = "Заблокировать карту")
  public ResponseEntity<BankCardDTO> blockCard(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long cardId) {

    BankCardDTO blockedCard = userCardServiceImpl.requestCardBlock(cardId, userId);
    return ResponseEntity.ok(blockedCard);
  }

  @PostMapping("/transfer")
  @Operation(summary = "Перевод между своими картами")
  public ResponseEntity<TransferResponseDTO> transferBetweenCards(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody TransferRequestDTO requestDTO) {

    TransferResponseDTO response = userCardServiceImpl.transferBetweenCards(userId, requestDTO);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/balance/total")
  @Operation(summary = "Получить общий баланс")
  public ResponseEntity<BigDecimal> getTotalBalance(@AuthenticationPrincipal Long userId) {
    BigDecimal balance = userCardServiceImpl.getTotalBalance(userId);
    return ResponseEntity.ok(balance);
  }

  @GetMapping("/search")
  @Operation(summary = "Поиск карт по владельцу")
  public ResponseEntity<Page<BankCardDTO>> searchCards(
      @AuthenticationPrincipal Long userId,
      @RequestParam String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<BankCardDTO> cards = userCardServiceImpl.searchUserCards(userId, query, pageable);
    return ResponseEntity.ok(cards);
  }
}