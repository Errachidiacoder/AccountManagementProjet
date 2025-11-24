package com.Account.Management.Project.domain.exception;

import java.util.UUID;

/**
 *  levée lorsqu'un utilisateur n'est pas trouvé.
 */
public class UserNotFoundException extends RuntimeException {

    private final UUID userId;

    public UserNotFoundException(String message) {
        super(message);
        this.userId = null;
    }

    public UserNotFoundException(UUID userId) {
        super(String.format("Utilisateur non trouvé avec l'ID: %s", userId));
        this.userId = userId;
    }

    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException(
                String.format("Utilisateur non trouvé avec l'email: %s", email));
    }

    public UUID getUserId() {
        return userId;
    }
}
