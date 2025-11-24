package com.Account.Management.Project.infrastructure.adapter.api;


import com.Account.Management.Project.domain.model.AccountStatement;
import com.Account.Management.Project.port.input.StatementUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
  la génération des relevés de compte.
 */
@RestController
@RequestMapping("/api/v1/statements")
@CrossOrigin(origins = "*")
public class StatementController {

    private final StatementUseCase statementUseCase;

    public StatementController(StatementUseCase statementUseCase) {
        this.statementUseCase = statementUseCase;
    }

    //  DTOs

    public record StatementResponse(
            UUID id,
            UUID accountId,
            String accountNumber,
            String accountHolderName,
            String openingBalance,
            String closingBalance,
            String periodStart,
            String periodEnd,
            int transactionCount,
            String generatedAt
    ) {
        public static StatementResponse fromDomain(AccountStatement s) {
            return new StatementResponse(
                    s.getId(),
                    s.getAccountId(),
                    s.getAccountNumber(),
                    s.getAccountHolderName(),
                    s.getOpeningBalance() != null ? s.getOpeningBalance().toFormattedString() : null,
                    s.getClosingBalance() != null ? s.getClosingBalance().toFormattedString() : null,
                    s.getPeriodStart().toString(),
                    s.getPeriodEnd().toString(),
                    s.getTransactionCount(),
                    s.getGeneratedAt().toString()
            );
        }
    }

    //  Endpoints

    /**
     * GET /api/v1/statements/account/{accountId} - Générer un relevé
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<StatementResponse> generateStatement(
            @PathVariable UUID accountId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        AccountStatement statement = statementUseCase.generateStatement(accountId, start, end);
        return ResponseEntity.ok(StatementResponse.fromDomain(statement));
    }

    /**
     * GET /api/v1/statements/account/{accountId}/current-month - Relevé du mois
     */
    @GetMapping("/account/{accountId}/current-month")
    public ResponseEntity<StatementResponse> generateCurrentMonthStatement(
            @PathVariable UUID accountId) {
        AccountStatement statement = statementUseCase.generateCurrentMonthStatement(accountId);
        return ResponseEntity.ok(StatementResponse.fromDomain(statement));
    }

    /**
     * GET /api/v1/statements/account/{accountId}/last-days - Relevé des N derniers jours
     */
    @GetMapping("/account/{accountId}/last-days")
    public ResponseEntity<StatementResponse> generateStatementForLastDays(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "30") int days) {
        AccountStatement statement = statementUseCase.generateStatementForLastDays(accountId, days);
        return ResponseEntity.ok(StatementResponse.fromDomain(statement));
    }

    /**
     * GET /api/v1/statements/account/{accountId}/pdf - Télécharger le relevé en PDF
     */
    @GetMapping("/account/{accountId}/pdf")
    public ResponseEntity<byte[]> downloadStatementPdf(
            @PathVariable UUID accountId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        byte[] pdfBytes = statementUseCase.exportStatementToPdf(accountId, start, end);

        String filename = String.format("releve_%s_%s_to_%s.pdf",
                accountId.toString().substring(0, 8),
                start.toLocalDate(),
                end.toLocalDate());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * GET /api/v1/statements/account/{accountId}/current-month/pdf - PDF du mois
     */
    @GetMapping("/account/{accountId}/current-month/pdf")
    public ResponseEntity<byte[]> downloadCurrentMonthStatementPdf(@PathVariable UUID accountId) {
        byte[] pdfBytes = statementUseCase.exportCurrentMonthStatementToPdf(accountId);

        String filename = String.format("releve_%s_%s.pdf",
                accountId.toString().substring(0, 8),
                LocalDateTime.now().toLocalDate());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}