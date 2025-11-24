package com.Account.Management.Project.port.input;

import com.Account.Management.Project.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port d'entrée définissant les cas d'usage liés aux transactions.
 * Implémenté par TransactionService dans la couche application.
 * Les transactions sont stockées dans MongoDB.
 */
public interface TransactionUseCase {

    /**
     * Effectue un transfert local (même devise)
     * @param sourceAccountId Compte source
     * @param targetAccountId Compte destinataire
     * @param amount Montant à transférer
     * @param description Description/motif du transfert
     * @return La transaction créée
     */
    Transaction transferLocal(UUID sourceAccountId, UUID targetAccountId,
                              Double amount, String description);

    /**
     * Effectue un transfert Forex (devises différentes)
     * @param sourceAccountId Compte source
     * @param targetAccountId Compte destinataire
     * @param sourceAmount Montant en devise source
     * @param description Description/motif du transfert
     * @return La transaction créée avec taux de change appliqué
     */
    Transaction transferForex(UUID sourceAccountId, UUID targetAccountId,
                              Double sourceAmount, String description);

    /**
     * Récupère une transaction par son ID
     */
    Optional<Transaction> getTransactionById(String transactionId);

    /**
     * Récupère toutes les transactions d'un compte (entrant et sortant)
     */
    List<Transaction> getTransactionsByAccountId(UUID accountId);

    /**
     * Récupère les transactions d'un compte pour une période donnée
     */
    List<Transaction> getTransactionsByAccountIdAndPeriod(
            UUID accountId, LocalDateTime start, LocalDateTime end);

    /**
     * Récupère toutes les transactions d'un utilisateur
     */
    List<Transaction> getTransactionsByUserId(UUID userId);

    /**
     * Récupère les dernières N transactions d'un compte
     */
    List<Transaction> getRecentTransactions(UUID accountId, int limit);
}
