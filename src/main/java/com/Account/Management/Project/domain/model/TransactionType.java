package com.Account.Management.Project.domain.model;


public enum TransactionType {
    LOCAL("Transaction locale", "Transfert entre comptes de même devise"),
    FOREX("Transaction Forex", "Transfert entre comptes de devises différentes");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
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
     * Vérifie si le type est FOREX
     */
    public boolean isForex() {
        return this == FOREX;
    }

    /**
     * Vérifie si le type est LOCAL
     */
    public boolean isLocal() {
        return this == LOCAL;
    }
}