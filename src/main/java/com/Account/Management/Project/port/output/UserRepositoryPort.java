package com.Account.Management.Project.port.output;

import com.Account.Management.Project.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de sortie pour la persistance des utilisateurs.
 * Implémenté par JpaUserRepositoryAdapter dans la couche infrastructure.
 */
public interface UserRepositoryPort {

    /**
     * Sauvegarde un utilisateur (création ou mise à jour)
     */
    User save(User user);

    /**
     * Trouve un utilisateur par son ID
     */
    Optional<User> findById(UUID id);

    /**
     * Trouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);

    /**
     * Récupère tous les utilisateurs
     */
    List<User> findAll();

    /**
     * Récupère tous les utilisateurs bloqués
     */
    List<User> findAllBlocked();

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Supprime un utilisateur par son ID
     */
    void deleteById(UUID id);

    /**
     * Vérifie si un utilisateur existe
     */
    boolean existsById(UUID id);
}
