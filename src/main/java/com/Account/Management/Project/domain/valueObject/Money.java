package com.Account.Management.Project.domain.valueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object représentant une somme d'argent avec sa devise
 * Immuable - toute opération retourne une nouvelle instance
 * Utilise BigDecimal pour éviter les erreurs d'arrondi
 */
public class Money {
    private final BigDecimal amount;    // Montant avec précision
    private final Currency currency;    // Devise associée

    // Constante pour le nombre de décimales
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    /**
     * Constructeur avec BigDecimal
     */
    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Le montant ne peut pas être null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("La devise ne peut pas être null");
        }
        this.amount = amount.setScale(SCALE, ROUNDING);
        this.currency = currency;
    }

    /**
     * Constructeur avec double (converti en BigDecimal)
     */
    public Money(double amount, Currency currency) {
        this(BigDecimal.valueOf(amount), currency);
    }

    /**
     * Factory method pour créer un montant de zéro
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    /**
     * Factory method à partir d'une chaîne
     */
    public static Money of(String amount, Currency currency) {
        return new Money(new BigDecimal(amount), currency);
    }

    // Opérations arithmétiques (retournent de nouvelles instances)

    /**
     * Addition de deux montants (même devise requise)
     */
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Soustraction de deux montants (même devise requise)
     */
    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Multiplication par un facteur
     */
    public Money multiply(double factor) {
        return new Money(
                this.amount.multiply(BigDecimal.valueOf(factor)),
                this.currency
        );
    }

    /**
     * Division par un diviseur
     */
    public Money divide(double divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division par zéro");
        }
        return new Money(
                this.amount.divide(BigDecimal.valueOf(divisor), SCALE, ROUNDING),
                this.currency
        );
    }

    // Méthodes de comparaison

    /**
     * Vérifie si ce montant est supérieur ou égal à un autre
     */
    public boolean isGreaterOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    /**
     * Vérifie si ce montant est strictement supérieur
     */
    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Vérifie si ce montant est inférieur ou égal
     */
    public boolean isLessOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) <= 0;
    }

    /**
     * Vérifie si le montant est positif
     */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Vérifie si le montant est négatif
     */
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Vérifie si le montant est zéro
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Valide que deux montants ont la même devise
     */
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    String.format("Devises incompatibles: %s et %s",
                            this.currency, other.currency));
        }
    }

    // Getters

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public double getAmountAsDouble() {
        return amount.doubleValue();
    }

    // equals, hashCode, toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 &&
                currency == money.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("%s %s",
                amount.setScale(SCALE, ROUNDING).toPlainString(),
                currency.getSymbol());
    }

    /**
     * Format pour affichage avec code devise
     */
    public String toFormattedString() {
        return String.format("%,.2f %s", amount, currency.getCode());
    }
}
