package com.Account.Management.Project.application.service;


import com.Account.Management.Project.domain.exception.AccountNotFoundException;
import com.Account.Management.Project.domain.exception.UserNotFoundException;
import com.Account.Management.Project.domain.model.AccountStatement;
import com.Account.Management.Project.domain.model.BankAccount;
import com.Account.Management.Project.domain.model.Transaction;
import com.Account.Management.Project.domain.model.User;
import com.Account.Management.Project.port.input.StatementUseCase;
import com.Account.Management.Project.port.output.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

/**
 * gerr la génération des relevés de compte.
 * Implémente le port d'entrée StatementUseCase.
 */
@Service
@Transactional(readOnly = true)
public class StatementService implements StatementUseCase {

    private final AccountRepositoryPort accountRepository;
    private final UserRepositoryPort userRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final PdfGeneratorPort pdfGenerator;

    public StatementService(AccountRepositoryPort accountRepository,
                            UserRepositoryPort userRepository,
                            TransactionRepositoryPort transactionRepository,
                            PdfGeneratorPort pdfGenerator) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public AccountStatement generateStatement(UUID accountId,
                                              LocalDateTime startDate,
                                              LocalDateTime endDate) {
        // Récupérer le compte
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Récupérer l'utilisateur pour le nom
        User user = userRepository.findById(account.getUserId())
                .orElseThrow(() -> new UserNotFoundException(account.getUserId()));

        // Créer le relevé
        AccountStatement statement = new AccountStatement(
                accountId,
                account.getAccountNumber(),
                user.getFullName(),
                startDate,
                endDate
        );

        // Récupérer les transactions de la période
        List<Transaction> transactions = transactionRepository
                .findByAccountIdAndPeriod(accountId, startDate, endDate);

        // Ajouter les transactions au relevé
        transactions.forEach(statement::addTransaction);

        // Définir les soldes (simplifié - en production, calculer à partir de l'historique)
        statement.setOpeningBalance(account.getBalance());
        statement.setClosingBalance(account.getBalance());

        return statement;
    }

    @Override
    public AccountStatement generateCurrentMonthStatement(UUID accountId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0);
        return generateStatement(accountId, startOfMonth, now);
    }

    @Override
    public AccountStatement generateStatementForLastDays(UUID accountId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(days);
        return generateStatement(accountId, start, now);
    }

    @Override
    public byte[] exportStatementToPdf(UUID accountId,
                                       LocalDateTime startDate,
                                       LocalDateTime endDate) {
        // Générer le relevé
        AccountStatement statement = generateStatement(accountId, startDate, endDate);

        // Convertir en PDF via le port
        return pdfGenerator.generateStatementPdf(statement);
    }

    @Override
    public byte[] exportCurrentMonthStatementToPdf(UUID accountId) {
        AccountStatement statement = generateCurrentMonthStatement(accountId);

        String filename = String.format("releve_%s_%s",
                statement.getAccountNumber(),
                statement.getGeneratedAt().toLocalDate());

        return pdfGenerator.generateStatementPdf(statement, filename);
    }
}