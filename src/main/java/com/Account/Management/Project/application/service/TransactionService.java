package com.Account.Management.Project.application.service;


import com.Account.Management.Project.domain.exception.*;
import com.Account.Management.Project.domain.model.BankAccount;
import com.Account.Management.Project.domain.model.Transaction;
import com.Account.Management.Project.domain.model.User;
import com.Account.Management.Project.domain.valueObject.Currency;
import com.Account.Management.Project.domain.valueObject.Money;
import com.Account.Management.Project.infrastructure.annotation.LogTransaction;
import com.Account.Management.Project.port.input.TransactionUseCase;
import com.Account.Management.Project.port.output.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * gerr les transactions bancaires.
 * Implémente le port d'entrée TransactionUseCase.
 *
 * Les transactions sont automatiquement loggées dans mongodb
 * grâce à l'annotation @LogTransaction
 */
@Service
@Transactional
public class TransactionService implements TransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;
    private final UserRepositoryPort userRepository;
    private final CurrencyConversionPort currencyConversion;

    public TransactionService(TransactionRepositoryPort transactionRepository,
                              AccountRepositoryPort accountRepository,
                              UserRepositoryPort userRepository,
                              CurrencyConversionPort currencyConversion) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.currencyConversion = currencyConversion;
    }

    /**
     * Effectue un transfert local entre deux comptes de même devise.
     * L'annotation @LogTransaction capture automatiquement les détails
     * et les stocke dans MongoDB.
     */
    @Override
    @LogTransaction // Annotation AOP pour logging automatique
    public Transaction transferLocal(UUID sourceAccountId, UUID targetAccountId,
                                     Double amount, String description) {
        // Récupérer les comptes
        BankAccount sourceAccount = getAccountOrThrow(sourceAccountId);
        BankAccount targetAccount = getAccountOrThrow(targetAccountId);

        // Vérifier que les utilisateurs ne sont pas bloqués
        checkUserNotBlocked(sourceAccount.getUserId());
        checkUserNotBlocked(targetAccount.getUserId());

        // Vérifier que les devises sont identiques pour un transfert local
        if (!sourceAccount.getCurrency().equals(targetAccount.getCurrency())) {
            throw new InvalidCurrencyException(
                    "Transfert local impossible entre devises différentes. " +
                            "Utilisez transferForex pour les conversions de devises.");
        }

        // Créer le montant
        Money transferAmount = new Money(amount, sourceAccount.getCurrency());

        // Effectuer le transfert
        sourceAccount.debit(transferAmount);
        targetAccount.credit(transferAmount);

        // Sauvegarder les comptes
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        // Créer et sauvegarder la transaction
        Transaction transaction = Transaction.createLocalTransaction(
                sourceAccountId, targetAccountId, transferAmount, description);
        transaction.complete();

        return transactionRepository.save(transaction);
    }

    /**
     * Effectue un transfert Forex entre deux comptes de devises différentes.
     * Applique le taux de change via le port CurrencyConversionPort.
     */
    @Override
    @LogTransaction // Annotation AOP pour logging automatique
    public Transaction transferForex(UUID sourceAccountId, UUID targetAccountId,
                                     Double sourceAmount, String description) {
        // Récupérer les comptes
        BankAccount sourceAccount = getAccountOrThrow(sourceAccountId);
        BankAccount targetAccount = getAccountOrThrow(targetAccountId);

        // Vérifier que les utilisateurs ne sont pas bloqués
        checkUserNotBlocked(sourceAccount.getUserId());
        checkUserNotBlocked(targetAccount.getUserId());

        Currency sourceCurrency = sourceAccount.getCurrency();
        Currency targetCurrency = targetAccount.getCurrency();

        // Vérifier que la conversion est supportée
        if (!currencyConversion.isConversionSupported(sourceCurrency, targetCurrency)) {
            throw new InvalidCurrencyException(
                    sourceCurrency.getCode(), targetCurrency.getCode());
        }

        // Créer le montant source
        Money sourceMoneyAmount = new Money(sourceAmount, sourceCurrency);

        // Convertir le montant vers la devise cible
        Money targetMoneyAmount = currencyConversion.convert(
                sourceMoneyAmount, targetCurrency);

        // Récupérer le taux de change pour l'historique
        Double exchangeRate = currencyConversion.getExchangeRate(
                sourceCurrency, targetCurrency);

        // Effectuer le transfert
        sourceAccount.debit(sourceMoneyAmount);
        targetAccount.credit(targetMoneyAmount);

        // Sauvegarder les comptes
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        // Créer et sauvegarder la transaction Forex
        Transaction transaction = Transaction.createForexTransaction(
                sourceAccountId, targetAccountId,
                sourceMoneyAmount, targetMoneyAmount,
                exchangeRate, description);
        transaction.complete();

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountId(UUID accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountIdAndPeriod(
            UUID accountId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByAccountIdAndPeriod(accountId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUserId(UUID userId) {
        // Récupérer tous les comptes de l'utilisateur
        List<BankAccount> accounts = accountRepository.findByUserId(userId);

        // Collecter toutes les transactions de tous les comptes
        List<Transaction> allTransactions = new ArrayList<>();
        for (BankAccount account : accounts) {
            allTransactions.addAll(
                    transactionRepository.findByAccountId(account.getId()));
        }

        // Trier par date décroissante
        allTransactions.sort((t1, t2) ->
                t2.getCreatedAt().compareTo(t1.getCreatedAt()));

        return allTransactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getRecentTransactions(UUID accountId, int limit) {
        return transactionRepository.findRecentByAccountId(accountId, limit);
    }

    // Méthodes utilitaires privées

    private BankAccount getAccountOrThrow(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private void checkUserNotBlocked(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.isBlocked()) {
            throw new BlockedUserException(userId, user.getEmail());
        }
    }
}
