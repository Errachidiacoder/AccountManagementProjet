package com.Account.Management.Project.port.input;

import com.Account.Management.Project.domain.model.BankAccount;
import com.Account.Management.Project.domain.valueObject.Money;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port d'entrée définissant les cas d'usage liés aux comptes bancaires.
 * Implémenté par AccountService dans la couche application.
 */
public interface AccountUseCase {

    /**
     * Crée un nouveau compte bancaire pour un utilisateur
     * @param userId ID de l'utilisateur propriétaire
     * @param currencyCode Code devise (ex: "EUR", "USD")
     * @param initialDeposit Dépôt initial optionnel
     * @return Le compte créé
     */
    BankAccount createAccount(UUID userId, String currencyCode, Double initialDeposit);

    /**
     * Récupère un compte par son ID
     */
    Optional<BankAccount> getAccountById(UUID accountId);

    /**
     * Récupère un compte par son numéro
     */
    Optional<BankAccount> getAccountByNumber(String accountNumber);

    /**
     * Récupère tous les comptes d'un utilisateur
     */
    List<BankAccount> getAccountsByUserId(UUID userId);

    /**
     * Récupère le solde d'un compte
     */
    Money getBalance(UUID accountId);

    /**
     * Dépose de l'argent sur un compte
     * @param accountId ID du compte
     * @param amount Montant à déposer
     * @return Le compte mis à jour
     */
    BankAccount deposit(UUID accountId, Double amount);

    /**
     * Retire de l'argent d'un compte
     * @param accountId ID du compte
     * @param amount Montant à retirer
     * @return Le compte mis à jour
     */
    BankAccount withdraw(UUID accountId, Double amount);

    /**
     * Désactive un compte bancaire
     */
    BankAccount deactivateAccount(UUID accountId);

    /**
     * Réactive un compte bancaire
     */
    BankAccount activateAccount(UUID accountId);
}