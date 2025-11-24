package com.Account.Management.Project.domain.exception;
import java.util.UUID;

/**
 * levée lorsqu'un utilisateur bloqué tente d'effectuer une opération.
 * Les utilisateurs bloqués ne peuvent pas réaliser d'opérations bancaires.
 */
public class BlockedUserException extends RuntimeException {

    private final UUID userId;
    private final String userEmail;

    public BlockedUserException(String message) {
        super(message);
        this.userId = null;
        this.userEmail = null;
    }

    public BlockedUserException(UUID userId) {
        super(String.format(
                "L'utilisateur avec l'ID %s est bloqué et ne peut pas effectuer d'opérations.",
                userId));
        this.userId = userId;
        this.userEmail = null;
    }

    public BlockedUserException(UUID userId, String userEmail) {
        super(String.format(
                "L'utilisateur %s (ID: %s) est bloqué et ne peut pas effectuer d'opérations bancaires.",
                userEmail, userId));
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
