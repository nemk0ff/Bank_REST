package com.example.bankcards.util;

public interface EncryptionService {
  String encrypt(String data);
  String decrypt(String encryptedData);
}