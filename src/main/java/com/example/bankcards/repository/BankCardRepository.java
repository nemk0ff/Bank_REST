package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

  Page<BankCard> findByUserId(Long userId, Pageable pageable);

  List<BankCard> findByUserId(Long userId);

  List<BankCard> findByUserIdAndStatus(Long userId, CardStatus status);

  Optional<BankCard> findByCardNumber(String cardNumber);

  boolean existsByCardNumber(String cardNumber);

  Page<BankCard> findByCardHolderContainingIgnoreCase(String cardHolder, Pageable pageable);

  @Query("SELECT c FROM BankCard c WHERE c.expiryDate < :currentDate AND c.status = 'ACTIVE'")
  List<BankCard> findExpiredCards(@Param("currentDate") LocalDate currentDate);

  Page<BankCard> findByStatus(CardStatus status, Pageable pageable);

  Page<BankCard> findByBalanceGreaterThan(BigDecimal minBalance, Pageable pageable);

  Page<BankCard> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}