package com.Account.Management.Project.port.output;

import com.Account.Management.Project.domain.model.BankAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de sortie pour la persistance des comptes bancaires.
 * Implémenté par JpaAccountRepositoryAdapter dans la couche infrastructure.
 */
public interface AccountRepositoryPort {

    /**
     * Sauvegarde un compte bancaire
     */
    BankAccount save(BankAccount account);

    /**
     * Trouve un compte par son ID
     */
    Optional<BankAccount> findById(UUID id);

    /**
     * Trouve un compte par son numéro
     */
    Optional<BankAccount> findByAccountNumber(String accountNumber);

    /**
     * Trouve tous les comptes d'un utilisateur
     */
    List<BankAccount> findByUserId(UUID userId);

    /**
     * Récupère tous les comptes
     */
    List<BankAccount> findAll();

    /**
     * Supprime un compte par son ID
     */
    void deleteById(UUID id);

    /**
     * Vérifie si un compte existe
     */
    boolean existsById(UUID id);

    /**
     * Vérifie si un numéro de compte existe
     */
    boolean existsByAccountNumber(String accountNumber);
}
