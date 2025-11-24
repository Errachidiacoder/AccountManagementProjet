package com.Account.Management.Project.domain.exception;

/**
  levée lors d'une opération avec une devise invalide ou non supportée.
 */
public class InvalidCurrencyException extends RuntimeException {

    private final String sourceCurrency;
    private final String targetCurrency;

    public InvalidCurrencyException(String message) {
        super(message);
        this.sourceCurrency = null;
        this.targetCurrency = null;
    }

    public InvalidCurrencyException(String sourceCurrency, String targetCurrency) {
        super(String.format(
                "Conversion de devise non supportée: %s vers %s",
                sourceCurrency, targetCurrency));
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }
}