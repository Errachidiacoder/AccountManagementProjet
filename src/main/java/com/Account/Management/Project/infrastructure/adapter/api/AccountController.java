package com.Account.Management.Project.infrastructure.adapter.api;


import com.Account.Management.Project.domain.model.BankAccount;
import com.Account.Management.Project.domain.valueObject.Money;
import com.Account.Management.Project.port.input.AccountUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
  les opérations sur les comptes bancaires.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountUseCase accountUseCase;

    public AccountController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    // DTOs

    public record CreateAccountRequest(
            UUID userId,
            String currencyCode,
            Double initialDeposit
    ) {}

    public record DepositWithdrawRequest(
            Double amount
    ) {}

    public record AccountResponse(
            UUID id,
            String accountNumber,
            UUID userId,
            String balance,
            String currency,
            boolean active,
            String createdAt
    ) {
        public static AccountResponse fromDomain(BankAccount account) {
            return new AccountResponse(
                    account.getId(),
                    account.getAccountNumber(),
                    account.getUserId(),
                    account.getBalance().toFormattedString(),
                    account.getCurrency().getCode(),
                    account.isActive(),
                    account.getCreatedAt().toString()
            );
        }
    }

    public record BalanceResponse(
            String balance,
            String currency
    ) {
        public static BalanceResponse fromMoney(Money money) {
            return new BalanceResponse(
                    money.toFormattedString(),
                    money.getCurrency().getCode()
            );
        }
    }

    //Endpoints

    /**
     * POST /api/v1/accounts - Créer un nouveau compte bancaire
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        BankAccount account = accountUseCase.createAccount(
                request.userId(),
                request.currencyCode(),
                request.initialDeposit()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AccountResponse.fromDomain(account));
    }

    /**
     * GET /api/v1/accounts/{id} - Récupérer un compte par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable UUID id) {
        return accountUseCase.getAccountById(id)
                .map(account -> ResponseEntity.ok(AccountResponse.fromDomain(account)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/accounts/number/{accountNumber} - Récupérer par numéro de compte
     */
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(@PathVariable String accountNumber) {
        return accountUseCase.getAccountByNumber(accountNumber)
                .map(account -> ResponseEntity.ok(AccountResponse.fromDomain(account)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/accounts/user/{userId} - Récupérer les comptes d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUserId(@PathVariable UUID userId) {
        List<AccountResponse> accounts = accountUseCase.getAccountsByUserId(userId)
                .stream()
                .map(AccountResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(accounts);
    }

    /**
     * GET /api/v1/accounts/{id}/balance - Récupérer le solde d'un compte
     */
    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID id) {
        Money balance = accountUseCase.getBalance(id);
        return ResponseEntity.ok(BalanceResponse.fromMoney(balance));
    }

    /**
     * POST /api/v1/accounts/{id}/deposit - Déposer de l'argent
     */
    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable UUID id,
            @RequestBody DepositWithdrawRequest request) {
        BankAccount account = accountUseCase.deposit(id, request.amount());
        return ResponseEntity.ok(AccountResponse.fromDomain(account));
    }

    /**
     * POST /api/v1/accounts/{id}/withdraw - Retirer de l'argent
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @PathVariable UUID id,
            @RequestBody DepositWithdrawRequest request) {
        BankAccount account = accountUseCase.withdraw(id, request.amount());
        return ResponseEntity.ok(AccountResponse.fromDomain(account));
    }

    /**
     * POST /api/v1/accounts/{id}/deactivate - Désactiver un compte
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<AccountResponse> deactivateAccount(@PathVariable UUID id) {
        BankAccount account = accountUseCase.deactivateAccount(id);
        return ResponseEntity.ok(AccountResponse.fromDomain(account));
    }

    /**
     * POST /api/v1/accounts/{id}/activate - Activer un compte
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<AccountResponse> activateAccount(@PathVariable UUID id) {
        BankAccount account = accountUseCase.activateAccount(id);
        return ResponseEntity.ok(AccountResponse.fromDomain(account));
    }
}