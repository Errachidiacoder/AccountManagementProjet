package com.Account.Management.Project.domain.exception;


import java.util.UUID;

/**
 * leve lorsqu'un compte bancaire n'est pas trouvé.
 */
public class AccountNotFoundException extends RuntimeException {

    private final UUID accountId;
    private final String accountNumber;

    public AccountNotFoundException(String message) {
        super(message);
        this.accountId = null;
        this.accountNumber = null;
    }

    public AccountNotFoundException(UUID accountId) {
        super(String.format("Compte bancaire non trouvé avec l'ID: %s", accountId));
        this.accountId = accountId;
        this.accountNumber = null;
    }

    public static AccountNotFoundException withAccountNumber(String accountNumber) {
        AccountNotFoundException ex = new AccountNotFoundException(
                String.format("Compte bancaire non trouvé avec le numéro: %s", accountNumber));
        return ex;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}