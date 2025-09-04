package com.example.bankcards.controller.card;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.card.CreateCardRequestDTO;
import com.example.bankcards.service.card.AdminCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Admin Card Management", description = "Управление картами для администраторов")
public class AdminCardController {

  private final AdminCardService adminCardService;

  @GetMapping("/user/{userId}/all")
  @Operation(summary = "Получить все карты пользователя (без пагинации)")
  public ResponseEntity<List<BankCardDTO>> getAllCardsByUser(@PathVariable Long userId) {
    List<BankCardDTO> cards = adminCardService.getCardsByUser(userId);
    return ResponseEntity.ok(cards);
  }

  @PostMapping
  @Operation(summary = "Создать новую карту")
  public ResponseEntity<BankCardDTO> createCard(@Valid @RequestBody CreateCardRequestDTO requestDTO) {
    BankCardDTO createdCard = adminCardService.createCard(requestDTO);
    return ResponseEntity.ok(createdCard);
  }

  @PostMapping("/{cardId}/block")
  @Operation(summary = "Заблокировать карту")
  public ResponseEntity<BankCardDTO> blockCard(@PathVariable Long cardId) {
    BankCardDTO blockedCard = adminCardService.blockCard(cardId);
    return ResponseEntity.ok(blockedCard);
  }

  @PostMapping("/{cardId}/activate")
  @Operation(summary = "Активировать карту")
  public ResponseEntity<BankCardDTO> activateCard(@PathVariable Long cardId) {
    BankCardDTO activatedCard = adminCardService.activateCard(cardId);
    return ResponseEntity.ok(activatedCard);
  }

  @DeleteMapping("/{cardId}")
  @Operation(summary = "Удалить карту")
  public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
    adminCardService.deleteCard(cardId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "Получить все карты с пагинацией")
  public ResponseEntity<Page<BankCardDTO>> getAllCards(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {

    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<BankCardDTO> cards = adminCardService.getAllCards(pageable);
    return ResponseEntity.ok(cards);
  }

  @GetMapping("/{cardId}")
  @Operation(summary = "Получить карту по ID")
  public ResponseEntity<BankCardDTO> getCardById(@PathVariable Long cardId) {
    BankCardDTO card = adminCardService.getCardById(cardId);
    return ResponseEntity.ok(card);
  }

  @GetMapping("/user/{userId}")
  @Operation(summary = "Получить карты пользователя")
  public ResponseEntity<Page<BankCardDTO>> getCardsByUser(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<BankCardDTO> cards = adminCardService.getCardsByUser(userId, pageable);
    return ResponseEntity.ok(cards);
  }
}