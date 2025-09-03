package com.example.bankcards.service.card;

import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.encryption.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCardService {

  private final BankCardRepository cardRepository;
  private final UserRepository userRepository;
  private final EncryptionService encryptionService;
}