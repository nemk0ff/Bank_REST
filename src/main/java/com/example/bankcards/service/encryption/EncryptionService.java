package com.example.bankcards.service.encryption;

public interface EncryptionService {
  String encrypt(String data);
  String decrypt(String encryptedData);
}