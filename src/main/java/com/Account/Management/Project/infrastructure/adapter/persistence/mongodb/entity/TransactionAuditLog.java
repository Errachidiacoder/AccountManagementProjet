package com.Account.Management.Project.infrastructure.adapter.persistence.mongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Document MongoDB pour stocker les logs d'audit des transactions.
 * Séparé des transactions elles-mêmes pour ne pas polluer les données métier.
 */
@Document(collection = "transaction_audit_logs")
public class TransactionAuditLog {

    @Id
    private String id;

    private String transactionId;       // Référence à la transaction
    private String correlationId;       // ID de corrélation pour traçabilité
    private String operation;           // Nom de l'opération (transferLocal, transferForex)
    private String status;              // SUCCESS, FAILURE
    private Long executionTimeMs;       // Temps d'exécution en ms
    private String errorMessage;        // Message d'erreur si échec
    private String errorType;           // Type d'exception si échec
    private String sourceAccountId;
    private String targetAccountId;
    private String amount;
    private LocalDateTime timestamp;

    public TransactionAuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    // Factory method pour succès
    public static TransactionAuditLog success(String transactionId,
                                              String correlationId,
                                              String operation,
                                              long executionTimeMs) {
        TransactionAuditLog log = new TransactionAuditLog();
        log.transactionId = transactionId;
        log.correlationId = correlationId;
        log.operation = operation;
        log.status = "SUCCESS";
        log.executionTimeMs = executionTimeMs;
        return log;
    }

    // Factory method pour échec
    public static TransactionAuditLog failure(String correlationId,
                                              String operation,
                                              Exception e,
                                              long executionTimeMs) {
        TransactionAuditLog log = new TransactionAuditLog();
        log.correlationId = correlationId;
        log.operation = operation;
        log.status = "FAILURE";
        log.executionTimeMs = executionTimeMs;
        log.errorMessage = e.getMessage();
        log.errorType = e.getClass().getSimpleName();
        return log;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }
    public String getSourceAccountId() { return sourceAccountId; }
    public void setSourceAccountId(String sourceAccountId) { this.sourceAccountId = sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
    public void setTargetAccountId(String targetAccountId) { this.targetAccountId = targetAccountId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
