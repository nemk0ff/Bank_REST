package com.example.bankcards.service;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.dto.transfer.TransferRequestDTO;
import com.example.bankcards.dto.transfer.TransferResponseDTO;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCardService {

  Page<BankCardDTO> getUserCards(Long userId, Pageable pageable);

  Page<BankCardDTO> getUserActiveCards(Long userId, Pageable pageable);

  BankCardDTO getCardDetails(Long cardId, Long userId);

  BankCardDTO requestCardBlock(Long cardId, Long userId);

  TransferResponseDTO transferBetweenCards(Long userId, TransferRequestDTO requestDTO);

  BigDecimal getTotalBalance(Long userId);

  Page<BankCardDTO> searchUserCards(Long userId, String search, Pageable pageable);
}
