package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BankCardRepository extends JpaRepository<BankCard, Long> {

  List<BankCard> findByUserId(Long userId);

  List<BankCard> findByUserIdAndStatus(Long userId, CardStatus status);

  Optional<BankCard> findByCardNumber(String cardNumber);

  boolean existsByCardNumber(String cardNumber);

  @Query("SELECT c FROM BankCard c WHERE c.expiryDate < CURRENT_DATE AND c.status = 'ACTIVE'")
  List<BankCard> findExpiredCards();
}