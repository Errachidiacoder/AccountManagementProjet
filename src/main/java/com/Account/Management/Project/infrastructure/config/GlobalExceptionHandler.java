package com.Account.Management.Project.infrastructure.config;


import com.Account.Management.Project.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Gestionnaire global des exceptions pour l'api rest
 * Traduit les exceptions métier en réponses http appropriées.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * DTO pour les réponses d'erreur standardisées
     */
    public record ErrorResponse(
            String error,
            String message,
            String timestamp,
            int status
    ) {
        public static ErrorResponse of(String error, String message, HttpStatus status) {
            return new ErrorResponse(
                    error,
                    message,
                    LocalDateTime.now().toString(),
                    status.value()
            );
        }
    }

    /**
     * Gère les exceptions de fonds insuffisants
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException e) {
        logger.warn("Fonds insuffisants: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        "INSUFFICIENT_FUNDS",
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST
                ));
    }

    /**
     * Gère les exceptions d'utilisateur bloqué
     */
    @ExceptionHandler(BlockedUserException.class)
    public ResponseEntity<ErrorResponse> handleBlockedUser(BlockedUserException e) {
        logger.warn("Utilisateur bloqué: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(
                        "USER_BLOCKED",
                        e.getMessage(),
                        HttpStatus.FORBIDDEN
                ));
    }

    /**
     * Gère les exceptions de compte non trouvé
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException e) {
        logger.warn("Compte non trouvé: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        "ACCOUNT_NOT_FOUND",
                        e.getMessage(),
                        HttpStatus.NOT_FOUND
                ));
    }

    /**
     * Gère les exceptions d'utilisateur non trouvé
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        logger.warn("Utilisateur non trouvé: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        "USER_NOT_FOUND",
                        e.getMessage(),
                        HttpStatus.NOT_FOUND
                ));
    }

    /**
     * Gère les exceptions de devise invalide
     */
    @ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrency(InvalidCurrencyException e) {
        logger.warn("Devise invalide: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        "INVALID_CURRENCY",
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST
                ));
    }

    /**
     * Gère les arguments invalides
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("Argument invalide: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        "INVALID_ARGUMENT",
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST
                ));
    }

    /**
     * Gère toutes les autres exceptions (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        logger.error("Erreur inattendue: ", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        "INTERNAL_ERROR",
                        "Une erreur inattendue s'est produite",
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }
}
