package com.Account.Management.Project.infrastructure.adapter.persistence.mongodb.entity;


import com.Account.Management.Project.domain.model.Transaction;
import com.Account.Management.Project.domain.model.TransactionStatus;
import com.Account.Management.Project.domain.model.TransactionType;
import com.Account.Management.Project.domain.valueObject.Currency;
import com.Account.Management.Project.domain.valueObject.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Document mongodb représentant une transaction bancaire
 * Les transactions sont stockées séparément dans MongoDB pour:
 *  Haute performance en lecture (historique)
 *  Scalabilité horizontale
 *  Flexibilité du schéma pour les métadonnées
 */
@Document(collection = "transactions")
@CompoundIndex(name = "account_date_idx",
        def = "{'sourceAccountId': 1, 'createdAt': -1}")
public class TransactionDocument {

    @Id
    private String id;

    @Indexed
    @Field("sourceAccountId")
    private String sourceAccountId;

    @Indexed
    @Field("targetAccountId")
    private String targetAccountId;

    // Montant source
    @Field("sourceAmount")
    private BigDecimal sourceAmount;

    @Field("sourceCurrency")
    private String sourceCurrency;

    // Montant cible (peut différer pour Forex)
    @Field("targetAmount")
    private BigDecimal targetAmount;

    @Field("targetCurrency")
    private String targetCurrency;

    @Field("type")
    private String type; // LOCAL ou FOREX

    @Field("status")
    private String status;

    @Field("exchangeRate")
    private Double exchangeRate;

    @Field("description")
    private String description;

    @Indexed
    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("processedAt")
    private LocalDateTime processedAt;

    // Constructeur par défaut
    public TransactionDocument() {}

    /**
     * Convertit un modèle de domaine Transaction en document MongoDB
     */
    public static TransactionDocument fromDomain(Transaction transaction) {
        TransactionDocument doc = new TransactionDocument();
        doc.id = transaction.getId();
        doc.sourceAccountId = transaction.getSourceAccountId().toString();
        doc.targetAccountId = transaction.getTargetAccountId().toString();

        // Montant source
        doc.sourceAmount = transaction.getSourceAmount().getAmount();
        doc.sourceCurrency = transaction.getSourceAmount().getCurrency().getCode();

        // Montant cible
        doc.targetAmount = transaction.getTargetAmount().getAmount();
        doc.targetCurrency = transaction.getTargetAmount().getCurrency().getCode();

        doc.type = transaction.getType().name();
        doc.status = transaction.getStatus().name();
        doc.exchangeRate = transaction.getExchangeRate();
        doc.description = transaction.getDescription();
        doc.createdAt = transaction.getCreatedAt();
        doc.processedAt = transaction.getProcessedAt();

        return doc;
    }

    /**
     * Convertit ce document MongoDB en modèle de domaine Transaction
     */
    public Transaction toDomain() {
        Transaction transaction = new Transaction();
        transaction.setId(this.id);
        transaction.setSourceAccountId(UUID.fromString(this.sourceAccountId));
        transaction.setTargetAccountId(UUID.fromString(this.targetAccountId));

        // Reconstituer les objets Money
        Currency srcCurrency = Currency.fromCode(this.sourceCurrency);
        Currency tgtCurrency = Currency.fromCode(this.targetCurrency);

        transaction.setSourceAmount(new Money(this.sourceAmount, srcCurrency));
        transaction.setTargetAmount(new Money(this.targetAmount, tgtCurrency));

        transaction.setType(TransactionType.valueOf(this.type));
        transaction.setStatus(TransactionStatus.valueOf(this.status));
        transaction.setExchangeRate(this.exchangeRate);
        transaction.setDescription(this.description);
        transaction.setCreatedAt(this.createdAt);
        transaction.setProcessedAt(this.processedAt);

        return transaction;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSourceAccountId() { return sourceAccountId; }
    public void setSourceAccountId(String sourceAccountId) { this.sourceAccountId = sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
    public void setTargetAccountId(String targetAccountId) { this.targetAccountId = targetAccountId; }
    public BigDecimal getSourceAmount() { return sourceAmount; }
    public void setSourceAmount(BigDecimal sourceAmount) { this.sourceAmount = sourceAmount; }
    public String getSourceCurrency() { return sourceCurrency; }
    public void setSourceCurrency(String sourceCurrency) { this.sourceCurrency = sourceCurrency; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(Double exchangeRate) { this.exchangeRate = exchangeRate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
