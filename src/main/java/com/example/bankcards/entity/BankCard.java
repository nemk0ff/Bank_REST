package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_cards")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, unique = true)
    private String cardNumber;

    @Column(name = "masked_number", nullable = false, length = 19)
    private String maskedNumber;

    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CardStatus status;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bank_cards_user"))
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return CardStatus.ACTIVE.equals(this.status);
    }

    public boolean isBlocked() {
        return CardStatus.BLOCKED.equals(this.status);
    }

    public boolean isExpired() {
        return CardStatus.EXPIRED.equals(this.status);
    }
}