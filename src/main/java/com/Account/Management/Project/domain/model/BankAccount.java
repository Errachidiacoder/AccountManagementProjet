package com.Account.Management.Project.domain.model;


import com.Account.Management.Project.domain.valueObject.Currency;
import com.Account.Management.Project.domain.valueObject.Money;
import com.Account.Management.Project.domain.exception.InsufficientFundsException;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Chaque compte est associé à un utilisateur et possède une devise
 */
public class BankAccount {
    private UUID id;
    private String accountNumber;     // Numéro de compte unique (IBAN simplifié)
    private UUID userId;              // Référence vers l'utilisateur propriétaire
    private Money balance;            // Solde du compte avec devise
    private boolean active;           // État du compte
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeur par défaut
    public BankAccount() {
        this.id = UUID.randomUUID();
        this.accountNumber = generateAccountNumber();
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructeur avec utilisateur et devise
    public BankAccount(UUID userId, Currency currency) {
        this();
        this.userId = userId;
        this.balance = new Money(0.0, currency);
    }

    // Constructeur complet
    public BankAccount(UUID userId, Money initialBalance) {
        this();
        this.userId = userId;
        this.balance = initialBalance;
    }

    /**
     * Génère un numéro de compte unique au format simplifié
     */
    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() +
                String.format("%04d", (int)(Math.random() * 10000));
    }

    // Méthodes métier

    /**
     * Crédite le compte d'un montant donné
     * @param amount Montant à créditer (doit être dans la même devise)
     */
    public void credit(Money amount) {
        if (!amount.getCurrency().equals(balance.getCurrency())) {
            throw new IllegalArgumentException(
                    "La devise du montant ne correspond pas à celle du compte");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Débite le compte d'un montant donné
     * @param amount Montant à débiter
     * @throws InsufficientFundsException si le solde est insuffisant
     */
    public void debit(Money amount) throws InsufficientFundsException {
        if (!amount.getCurrency().equals(balance.getCurrency())) {
            throw new IllegalArgumentException(
                    "La devise du montant ne correspond pas à celle du compte");
        }
        if (!hasSufficientFunds(amount)) {
            throw new InsufficientFundsException(
                    "Solde insuffisant. Disponible: " + balance + ", Requis: " + amount);
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si le compte dispose de fonds suffisants
     */
    public boolean hasSufficientFunds(Money amount) {
        return balance.isGreaterOrEqual(amount);
    }

    /**
     * Récupère la devise du compte
     */
    public Currency getCurrency() {
        return balance.getCurrency();
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Money getBalance() { return balance; }
    public void setBalance(Money balance) { this.balance = balance; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
