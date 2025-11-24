package com.Account.Management.Project.port.input;

import com.Account.Management.Project.domain.model.Role;
import com.Account.Management.Project.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port d'entrée définissant les cas d'usage liés aux utilisateurs.
 * Implémenté par UserService dans la couche application.
 */
public interface UserUseCase {

    /**
     * Enregistre un nouvel utilisateur avec création automatique d'un compte bancaire
     * @param firstName Prénom
     * @param lastName Nom
     * @param email Email (unique)
     * @param password Mot de passe
     * @param role Rôle de l'utilisateur
     * @param currencyCode Code devise pour le compte bancaire (ex: "EUR")
     * @return L'utilisateur créé
     */
    User registerUser(String firstName, String lastName, String email,
                      String password, Role role, String currencyCode);

    /**
     * Récupère un utilisateur par son ID
     */
    Optional<User> getUserById(UUID userId);

    /**
     * Récupère un utilisateur par son email
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Liste tous les utilisateurs
     */
    List<User> getAllUsers();

    /**
     * Bloque un utilisateur (il ne pourra plus faire d'opérations bancaires)
     * @param userId ID de l'utilisateur à bloquer
     * @return L'utilisateur mis à jour
     */
    User blockUser(UUID userId);

    /**
     * Débloque un utilisateur
     * @param userId ID de l'utilisateur à débloquer
     * @return L'utilisateur mis à jour
     */
    User unblockUser(UUID userId);

    /**
     * Met à jour les informations d'un utilisateur
     */
    User updateUser(UUID userId, String firstName, String lastName, String email);

    /**
     * Supprime un utilisateur
     */
    void deleteUser(UUID userId);
}
