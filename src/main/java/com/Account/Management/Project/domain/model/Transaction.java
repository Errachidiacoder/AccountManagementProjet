package com.Account.Management.Project.domain.model;


import com.Account.Management.Project.domain.valueObject.Money;

import java.time.LocalDateTime;
import java.util.UUID;

import com.Account.Management.Project.domain.valueObject.Money;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * une transaction bancaire.
 * Stockée dans MongoDB pour historique et audit.
 *
 * CORRECTION: Les enums TransactionType et TransactionStatus sont maintenant
 * dans des fichiers séparés et publiques
 */
public class Transaction {
    private String id;                      // ID MongoDB (String pour compatibilité)
    private UUID sourceAccountId;           // Compte source
    private UUID targetAccountId;           // Compte destinataire
    private Money sourceAmount;             // Montant débité (devise source)
    private Money targetAmount;             // Montant crédité (devise cible)
    private TransactionType type;           // LOCAL ou FOREX
    private TransactionStatus status;       // État de la transaction
    private Double exchangeRate;            // Taux de change (null si LOCAL)
    private String description;             // Description/motif
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    // Constructeur par défaut
    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Factory method pour transaction locale
    public static Transaction createLocalTransaction(
            UUID sourceAccountId,
            UUID targetAccountId,
            Money amount,
            String description) {
        Transaction tx = new Transaction();
        tx.sourceAccountId = sourceAccountId;
        tx.targetAccountId = targetAccountId;
        tx.sourceAmount = amount;
        tx.targetAmount = amount; // Même montant pour LOCAL
        tx.type = TransactionType.LOCAL;
        tx.description = description;
        return tx;
    }

    // Factory method pour transaction Forex
    public static Transaction createForexTransaction(
            UUID sourceAccountId,
            UUID targetAccountId,
            Money sourceAmount,
            Money targetAmount,
            Double exchangeRate,
            String description) {
        Transaction tx = new Transaction();
        tx.sourceAccountId = sourceAccountId;
        tx.targetAccountId = targetAccountId;
        tx.sourceAmount = sourceAmount;
        tx.targetAmount = targetAmount;
        tx.type = TransactionType.FOREX;
        tx.exchangeRate = exchangeRate;
        tx.description = description;
        return tx;
    }

    // Méthodes métier

    /**
     * Marque la transaction comme complétée
     */
    public void complete() {
        this.status = TransactionStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Marque la transaction comme échouée
     */
    public void fail() {
        this.status = TransactionStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Annule la transaction (seulement si PENDING)
     */
    public void cancel() {
        if (!this.status.isCancellable()) {
            throw new IllegalStateException(
                    "La transaction ne peut pas être annulée dans l'état: " + this.status);
        }
        this.status = TransactionStatus.CANCELLED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si c'est une transaction Forex
     */
    public boolean isForex() {
        return type == TransactionType.FOREX;
    }

    /**
     * Vérifie si la transaction est terminée
     */
    public boolean isFinalized() {
        return status.isFinalized();
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public UUID getSourceAccountId() { return sourceAccountId; }
    public void setSourceAccountId(UUID sourceAccountId) { this.sourceAccountId = sourceAccountId; }
    public UUID getTargetAccountId() { return targetAccountId; }
    public void setTargetAccountId(UUID targetAccountId) { this.targetAccountId = targetAccountId; }
    public Money getSourceAmount() { return sourceAmount; }
    public void setSourceAmount(Money sourceAmount) { this.sourceAmount = sourceAmount; }
    public Money getTargetAmount() { return targetAmount; }
    public void setTargetAmount(Money targetAmount) { this.targetAmount = targetAmount; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public Double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(Double exchangeRate) { this.exchangeRate = exchangeRate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}