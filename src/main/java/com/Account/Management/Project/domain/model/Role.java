package com.Account.Management.Project.domain.model;


/**
 * les rôles possibles d'un utilisateur.
 * CUSTOMER: Client standard pouvant effectuer des opérations bancaires
 * MANAGER: Gestionnaire avec des privilèges étendus
 */
public enum Role {
    CUSTOMER("Client"),
    MANAGER("Gestionnaire");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}