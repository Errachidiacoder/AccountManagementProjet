package com.Account.Management.Project.application.service;


import com.Account.Management.Project.domain.exception.*;
import com.Account.Management.Project.domain.model.BankAccount;
import com.Account.Management.Project.domain.model.User;
import com.Account.Management.Project.domain.valueObject.Currency;
import com.Account.Management.Project.domain.valueObject.Money;
import com.Account.Management.Project.port.input.AccountUseCase;
import com.Account.Management.Project.port.output.AccountRepositoryPort;
import com.Account.Management.Project.port.output.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 gere les opérations sur les comptes bancaires.
 * Implémente le port d'entrée AccountUseCase.
 */
@Service
@Transactional
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort accountRepository;
    private final UserRepositoryPort userRepository;

    public AccountService(AccountRepositoryPort accountRepository,
                          UserRepositoryPort userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BankAccount createAccount(UUID userId, String currencyCode, Double initialDeposit) {
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Vérifier que l'utilisateur n'est pas bloqué
        if (user.isBlocked()) {
            throw new BlockedUserException(userId, user.getEmail());
        }

        // Créer le compte avec la devise spécifiée
        Currency currency = Currency.fromCode(currencyCode);
        BankAccount account = new BankAccount(userId, currency);

        // Appliquer le dépôt initial si spécifié
        if (initialDeposit != null && initialDeposit > 0) {
            Money deposit = new Money(initialDeposit, currency);
            account.credit(deposit);
        }

        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BankAccount> getAccountById(UUID accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BankAccount> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccount> getAccountsByUserId(UUID userId) {
        return accountRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Money getBalance(UUID accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return account.getBalance();
    }

    /**
     * dépose de l'argent sur un compte.
     * Vérifie que le propriétaire du compte n'est pas bloqué.
     */
    @Override
    public BankAccount deposit(UUID accountId, Double amount) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Vérifier que l'utilisateur n'est pas bloqué
        checkUserNotBlocked(account.getUserId());

        Money depositAmount = new Money(amount, account.getCurrency());
        account.credit(depositAmount);

        return accountRepository.save(account);
    }

    /**
     * retire de l'argent d'un compte.
     * Vérifie le solde suffisant et que l'utilisateur n'est pas bloqué.
     */
    @Override
    public BankAccount withdraw(UUID accountId, Double amount) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Vérifier que l'utilisateur n'est pas bloqué
        checkUserNotBlocked(account.getUserId());

        Money withdrawAmount = new Money(amount, account.getCurrency());
        account.debit(withdrawAmount); // Lève InsufficientFundsException si solde insuffisant

        return accountRepository.save(account);
    }

    @Override
    public BankAccount deactivateAccount(UUID accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.setActive(false);
        return accountRepository.save(account);
    }

    @Override
    public BankAccount activateAccount(UUID accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.setActive(true);
        return accountRepository.save(account);
    }

    /**
     * vérifie que l'utilisateur n'est pas bloqué.
     * Méthode utilitaire utilisée avant toute opération bancaire.
     */
    private void checkUserNotBlocked(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.isBlocked()) {
            throw new BlockedUserException(userId, user.getEmail());
        }
    }
}
