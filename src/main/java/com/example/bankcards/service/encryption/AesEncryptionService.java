package com.example.bankcards.service.encryption;

import com.example.bankcards.exception.transfer.EncryptionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class AesEncryptionService implements EncryptionService {

  private static final String ALGORITHM = "AES";
  private final SecretKeySpec secretKey;

  public AesEncryptionService(@Value("${encryption.secret-key}") String secretKey) {
    if (secretKey.length() != 16 && secretKey.length() != 24 && secretKey.length() != 32) {
      throw new IllegalArgumentException("Secret key must be 16, 24 or 32 characters long");
    }
    this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
  }

  @Override
  public String encrypt(String data) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      log.error("Error encrypting data", e);
      throw new EncryptionException("Failed to encrypt data", e);
    }
  }

  @Override
  public String decrypt(String encryptedData) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
      byte[] decryptedBytes = cipher.doFinal(decodedBytes);
      return new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("Error decrypting data", e);
      throw new EncryptionException("Failed to decrypt data", e);
    }
  }
}