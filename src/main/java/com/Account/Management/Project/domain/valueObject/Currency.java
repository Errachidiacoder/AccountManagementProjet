package com.Account.Management.Project.domain.valueObject;

import java.util.Arrays;
import java.util.List;

/**

 * value object immuable :une devise est définie par son code et son symbole
 */
public enum Currency {
    EUR("EUR", "€", "Euro"),
    USD("USD", "$", "Dollar américain"),
    GBP("GBP", "£", "Livre sterling"),
    MAD("MAD", "DH", "Dirham marocain"),
    JPY("JPY", "¥", "Yen japonais"),
    CHF("CHF", "CHF", "Franc suisse");

    private final String code;      // Code ISO 4217
    private final String symbol;    // Symbole de la devise
    private final String name;      // Nom complet

    Currency(String code, String symbol, String name) {
        this.code = code;
        this.symbol = symbol;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }

    /**
     * Trouve une devise par son code
     * @param code Code ISO de la devise
     * @return La devise correspondante
     * @throws IllegalArgumentException si le code est invalide
     */
    public static Currency fromCode(String code) {
        return Arrays.stream(values())
                .filter(c -> c.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Devise non supportée: " + code));
    }

    /**
     * Vérifie si une devise est supportée
     */
    public static boolean isSupported(String code) {
        return Arrays.stream(values())
                .anyMatch(c -> c.code.equalsIgnoreCase(code));
    }

    /**
     * Retourne la liste des codes de devises supportées
     */
    public static List<String> getSupportedCodes() {
        return Arrays.stream(values())
                .map(Currency::getCode)
                .toList();
    }

    @Override
    public String toString() {
        return code;
    }
}