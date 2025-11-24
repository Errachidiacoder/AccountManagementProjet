package com.Account.Management.Project.infrastructure.adapter.currency;


import com.Account.Management.Project.domain.valueObject.Currency;
import com.Account.Management.Project.domain.valueObject.Money;
import com.Account.Management.Project.port.output.CurrencyConversionPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter implémentant le port de sortie CurrencyConversionPort
 *
 * Dans cette implémentation, les taux de change sont statiques
 * En production, cet adapter pourrait appeler une API externe
 */
@Component
public class CurrencyConversionAdapter implements CurrencyConversionPort {

    // Taux de change par rapport à l'EUR (devise de base)
    // En production: charger depuis une API ou une base de données
    private static final Map<Currency, Double> RATES_TO_EUR = new HashMap<>();

    static {
        // Taux de change fictifs (1 EUR = X devise)
        RATES_TO_EUR.put(Currency.EUR, 1.0);
        RATES_TO_EUR.put(Currency.USD, 1.08);    // 1 EUR = 1.08 USD
        RATES_TO_EUR.put(Currency.GBP, 0.86);    // 1 EUR = 0.86 GBP
        RATES_TO_EUR.put(Currency.MAD, 10.85);   // 1 EUR = 10.85 MAD
        RATES_TO_EUR.put(Currency.JPY, 162.50);  // 1 EUR = 162.50 JPY
        RATES_TO_EUR.put(Currency.CHF, 0.94);    // 1 EUR = 0.94 CHF
    }

    @Override
    public Money convert(Money amount, Currency targetCurrency) {
        if (amount.getCurrency().equals(targetCurrency)) {
            // Même devise, pas de conversion nécessaire
            return amount;
        }

        Double rate = getExchangeRate(amount.getCurrency(), targetCurrency);
        double convertedAmount = amount.getAmountAsDouble() * rate;

        return new Money(convertedAmount, targetCurrency);
    }

    @Override
    public Double getExchangeRate(Currency source, Currency target) {
        if (source.equals(target)) {
            return 1.0;
        }

        // Convertir via EUR comme devise pivot
        // source -> EUR -> target
        Double sourceToEur = 1.0 / RATES_TO_EUR.get(source);
        Double eurToTarget = RATES_TO_EUR.get(target);

        return sourceToEur * eurToTarget;
    }

    @Override
    public boolean isConversionSupported(Currency source, Currency target) {
        // Toutes les devises définies dans l'enum sont supportées
        return RATES_TO_EUR.containsKey(source) &&
                RATES_TO_EUR.containsKey(target);
    }
}