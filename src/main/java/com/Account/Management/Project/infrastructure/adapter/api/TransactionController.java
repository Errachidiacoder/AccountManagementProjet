package com.Account.Management.Project.infrastructure.adapter.api;

import com.Account.Management.Project.domain.model.Transaction;
import com.Account.Management.Project.port.input.TransactionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
  pour les opérations de transaction.

 */
@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionUseCase transactionUseCase;

    public TransactionController(TransactionUseCase transactionUseCase) {
        this.transactionUseCase = transactionUseCase;
    }

   //dtos

    public record LocalTransferRequest(
            UUID sourceAccountId,
            UUID targetAccountId,
            Double amount,
            String description
    ) {}

    public record ForexTransferRequest(
            UUID sourceAccountId,
            UUID targetAccountId,
            Double sourceAmount,
            String description
    ) {}

    /**
     * dto pour la réponse transaction

     */
    public record TransactionResponse(
            String id,
            UUID sourceAccountId,
            UUID targetAccountId,
            String sourceAmount,
            String targetAmount,
            String type,
            String status,
            Double exchangeRate,
            String description,
            String createdAt,
            String processedAt
    ) {
        public static TransactionResponse fromDomain(Transaction tx) {
            return new TransactionResponse(
                    tx.getId(),
                    tx.getSourceAccountId(),
                    tx.getTargetAccountId(),
                    tx.getSourceAmount().toFormattedString(),
                    tx.getTargetAmount().toFormattedString(),
                    tx.getType().name(),      // CORRECTION: fonctionne maintenant
                    tx.getStatus().name(),    // CORRECTION: fonctionne maintenant
                    tx.getExchangeRate(),
                    tx.getDescription(),
                    tx.getCreatedAt().toString(),
                    tx.getProcessedAt() != null ? tx.getProcessedAt().toString() : null
            );
        }
    }

    //endpoints

    /**
     * POST /api/v1/transactions/local - Transfert local (même devise)
     */
    @PostMapping("/local")
    public ResponseEntity<TransactionResponse> transferLocal(@RequestBody LocalTransferRequest request) {
        Transaction transaction = transactionUseCase.transferLocal(
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount(),
                request.description()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TransactionResponse.fromDomain(transaction));
    }

    /**
     * POST /api/v1/transactions/forex - Transfert Forex (devises différentes)
     */
    @PostMapping("/forex")
    public ResponseEntity<TransactionResponse> transferForex(@RequestBody ForexTransferRequest request) {
        Transaction transaction = transactionUseCase.transferForex(
                request.sourceAccountId(),
                request.targetAccountId(),
                request.sourceAmount(),
                request.description()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TransactionResponse.fromDomain(transaction));
    }

    /**
     * GET /api/v1/transactions/{id} - Récupérer une transaction par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable String id) {
        return transactionUseCase.getTransactionById(id)
                .map(tx -> ResponseEntity.ok(TransactionResponse.fromDomain(tx)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/transactions/account/{accountId} - Transactions d'un compte
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(
            @PathVariable UUID accountId) {
        List<TransactionResponse> transactions = transactionUseCase
                .getTransactionsByAccountId(accountId)
                .stream()
                .map(TransactionResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(transactions);
    }

    /**
     * GET /api/v1/transactions/account/{accountId}/recent - Transactions récentes
     */
    @GetMapping("/account/{accountId}/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "10") int limit) {
        List<TransactionResponse> transactions = transactionUseCase
                .getRecentTransactions(accountId, limit)
                .stream()
                .map(TransactionResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(transactions);
    }

    /**
     * GET /api/v1/transactions/user/{userId} - Transactions d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUser(
            @PathVariable UUID userId) {
        List<TransactionResponse> transactions = transactionUseCase
                .getTransactionsByUserId(userId)
                .stream()
                .map(TransactionResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(transactions);
    }

    /**
     * GET /api/v1/transactions/account/{accountId}/period - Transactions par période
     */
    @GetMapping("/account/{accountId}/period")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByPeriod(
            @PathVariable UUID accountId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        List<TransactionResponse> transactions = transactionUseCase
                .getTransactionsByAccountIdAndPeriod(accountId, start, end)
                .stream()
                .map(TransactionResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(transactions);
    }
}
