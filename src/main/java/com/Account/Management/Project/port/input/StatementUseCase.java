package com.Account.Management.Project.port.input;

import com.Account.Management.Project.domain.model.AccountStatement;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Port d'entrée définissant les cas d'usage liés aux relevés de compte.
 * Implémenté par StatementService dans la couche application.
 */
public interface StatementUseCase {

    /**
     * Génère un relevé de compte pour une période donnée
     * @param accountId ID du compte
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Le relevé de compte
     */
    AccountStatement generateStatement(UUID accountId,
                                       LocalDateTime startDate,
                                       LocalDateTime endDate);

    /**
     * Génère un relevé de compte pour le mois en cours
     */
    AccountStatement generateCurrentMonthStatement(UUID accountId);

    /**
     * Génère un relevé de compte pour les N derniers jours
     */
    AccountStatement generateStatementForLastDays(UUID accountId, int days);

    /**
     * Exporte un relevé de compte en PDF
     * @param accountId ID du compte
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Bytes du fichier PDF
     */
    byte[] exportStatementToPdf(UUID accountId,
                                LocalDateTime startDate,
                                LocalDateTime endDate);

    /**
     * Exporte le relevé du mois en cours en PDF
     */
    byte[] exportCurrentMonthStatementToPdf(UUID accountId);
}