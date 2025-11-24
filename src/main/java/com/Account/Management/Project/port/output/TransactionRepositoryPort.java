package com.Account.Management.Project.port.output;

import com.Account.Management.Project.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de sortie pour la persistance des transactions.
 * Implémenté par MongoTransactionRepositoryAdapter dans la couche infrastructure.
 * Les transactions sont stockées dans MongoDB.
 */
public interface TransactionRepositoryPort {

    /**
     * Sauvegarde une transaction dans MongoDB
     */
    Transaction save(Transaction transaction);

    /**
     * Trouve une transaction par son ID
     */
    Optional<Transaction> findById(String id);

    /**
     * Trouve toutes les transactions d'un compte (source ou destination)
     */
    List<Transaction> findByAccountId(UUID accountId);

    /**
     * Trouve les transactions d'un compte pour une période
     */
    List<Transaction> findByAccountIdAndPeriod(UUID accountId,
                                               LocalDateTime start,
                                               LocalDateTime end);

    /**
     * Trouve les transactions où le compte est la source
     */
    List<Transaction> findBySourceAccountId(UUID accountId);

    /**
     * Trouve les transactions où le compte est la destination
     */
    List<Transaction> findByTargetAccountId(UUID accountId);

    /**
     * Récupère les dernières N transactions d'un compte
     */
    List<Transaction> findRecentByAccountId(UUID accountId, int limit);

    /**
     * Compte le nombre de transactions d'un compte
     */
    long countByAccountId(UUID accountId);
}
