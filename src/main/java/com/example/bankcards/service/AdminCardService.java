package com.example.bankcards.service;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.card.CreateCardRequestDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCardService {
  BankCardDTO createCard(CreateCardRequestDTO requestDTO);

  BankCardDTO blockCard(Long cardId);

  BankCardDTO activateCard(Long cardId);

  void deleteCard(Long cardId);

  Page<BankCardDTO> getAllCards(Pageable pageable);

  BankCardDTO getCardById(Long cardId);

  Page<BankCardDTO> getCardsByUser(Long userId, Pageable pageable);

  List<BankCardDTO> getCardsByUser(Long userId);
}
