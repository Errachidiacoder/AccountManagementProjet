package com.Account.Management.Project.domain.model;




import com.Account.Management.Project.domain.valueObject.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * represent un relevé de compte bancaire.
 * Contient l'historique des transactions pour une période donnée
 */
public class AccountStatement {
    private UUID id;
    private UUID accountId;
    private String accountNumber;
    private String accountHolderName;
    private Money openingBalance;           // Solde d'ouverture
    private Money closingBalance;           // Solde de clôture
    private LocalDateTime periodStart;      // Début de période
    private LocalDateTime periodEnd;        // Fin de période
    private List<Transaction> transactions; // Liste des transactions
    private LocalDateTime generatedAt;      // Date de génération

    public AccountStatement() {
        this.id = UUID.randomUUID();
        this.transactions = new ArrayList<>();
        this.generatedAt = LocalDateTime.now();
    }

    public AccountStatement(UUID accountId, String accountNumber, String accountHolderName,
                            LocalDateTime periodStart, LocalDateTime periodEnd) {
        this();
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    /**
     * Ajoute une transaction au relevé
     */
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    /**
     * Calcule le total des crédits sur la période
     */
    public Money getTotalCredits() {
        // Implémentation simplifiée - à adapter selon la devise
        return closingBalance;
    }

    /**
     * Calcule le total des débits sur la période
     */
    public Money getTotalDebits() {
        // Implémentation simplifiée
        return openingBalance;
    }

    /**
     * Nombre de transactions dans le relevé
     */
    public int getTransactionCount() {
        return transactions.size();
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public Money getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(Money openingBalance) { this.openingBalance = openingBalance; }
    public Money getClosingBalance() { return closingBalance; }
    public void setClosingBalance(Money closingBalance) { this.closingBalance = closingBalance; }
    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}