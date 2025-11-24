package com.Account.Management.Project.infrastructure.adapter.persistence.postgres.entity;


import com.Account.Management.Project.domain.model.BankAccount;
import com.Account.Management.Project.domain.valueObject.Currency;
import com.Account.Management.Project.domain.valueObject.Money;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant un compte bancaire dans Postgresql
 * Le solde est stocké avec sa devise séparément pour la persistance
 */
@Entity
@Table(name = "bank_accounts", indexes = {
        @Index(name = "idx_account_number", columnList = "account_number", unique = true),
        @Index(name = "idx_account_user", columnList = "user_id")
})
public class AccountEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "account_number", nullable = false, unique = true, length = 30)
    private String accountNumber;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructeur par défaut requis par JPA
    public AccountEntity() {}

    /**
     * Convertit un modèle de domaine BankAccount en entité JPA
     */
    public static AccountEntity fromDomain(BankAccount account) {
        AccountEntity entity = new AccountEntity();
        entity.id = account.getId();
        entity.accountNumber = account.getAccountNumber();
        entity.userId = account.getUserId();
        entity.balance = account.getBalance().getAmount();
        entity.currency = account.getBalance().getCurrency();
        entity.active = account.isActive();
        entity.createdAt = account.getCreatedAt();
        entity.updatedAt = account.getUpdatedAt();
        return entity;
    }

    /**
     * Convertit cette entité JPA en modèle de domaine BankAccount
     */
    public BankAccount toDomain() {
        BankAccount account = new BankAccount();
        account.setId(this.id);
        account.setAccountNumber(this.accountNumber);
        account.setUserId(this.userId);
        account.setBalance(new Money(this.balance, this.currency));
        account.setActive(this.active);
        account.setCreatedAt(this.createdAt);
        account.setUpdatedAt(this.updatedAt);
        return account;
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}