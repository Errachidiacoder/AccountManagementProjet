package com.Account.Management.Project.port.output;


import com.Account.Management.Project.domain.valueObject.Currency;
import com.Account.Management.Project.domain.valueObject.Money;

/**
 * Port de sortie pour la conversion de devises.
 * Peut être implémenté par un service externe ou une API de taux de change.
 */
public interface CurrencyConversionPort {

    /**
     * Convertit un montant d'une devise à une autre
     * @param amount Montant à convertir
     * @param targetCurrency Devise cible
     * @return Montant converti dans la devise cible
     */
    Money convert(Money amount, Currency targetCurrency);

    /**
     * Récupère le taux de change entre deux devises
     * @param source Devise source
     * @param target Devise cible
     * @return Taux de change
     */
    Double getExchangeRate(Currency source, Currency target);

    /**
     * Vérifie si la conversion est supportée entre deux devises
     */
    boolean isConversionSupported(Currency source, Currency target);
}
