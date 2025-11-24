package com.Account.Management.Project.domain.exception;

/**
 * levée lorsqu'un compte n'a pas assez de fonds pour une opération.
 * Cette exception fait partie du domaine métier.
 */
public class InsufficientFundsException extends RuntimeException {

    private final String accountNumber;
    private final Double requestedAmount;
    private final Double availableBalance;

    public InsufficientFundsException(String message) {
        super(message);
        this.accountNumber = null;
        this.requestedAmount = null;
        this.availableBalance = null;
    }

    public InsufficientFundsException(String accountNumber,
                                      Double requestedAmount,
                                      Double availableBalance) {
        super(String.format(
                "Fonds insuffisants sur le compte %s. " +
                        "Montant demandé: %.2f, Solde disponible: %.2f",
                accountNumber, requestedAmount, availableBalance));
        this.accountNumber = accountNumber;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public Double getAvailableBalance() {
        return availableBalance;
    }
}