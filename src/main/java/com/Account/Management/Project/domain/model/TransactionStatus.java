package com.Account.Management.Project.domain.model;



public enum TransactionStatus {
    PENDING("En attente", "Transaction en cours de traitement"),
    COMPLETED("Terminée", "Transaction complétée avec succès"),
    FAILED("Échouée", "Transaction échouée"),
    CANCELLED("Annulée", "Transaction annulée");

    private final String displayName;
    private final String description;

    TransactionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Vérifie si la transaction est terminée (succès ou échec)
     */
    public boolean isFinalized() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /**
     * Vérifie si la transaction est en succès
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Vérifie si la transaction peut être annulée
     */
    public boolean isCancellable() {
        return this == PENDING;
    }
}