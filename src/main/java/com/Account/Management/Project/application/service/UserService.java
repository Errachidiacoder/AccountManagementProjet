package com.Account.Management.Project.application.service;

import com.Account.Management.Project.domain.exception.UserNotFoundException;
import com.Account.Management.Project.domain.model.BankAccount;
import com.Account.Management.Project.domain.model.Role;
import com.Account.Management.Project.domain.model.User;
import com.Account.Management.Project.domain.valueObject.Currency;
import com.Account.Management.Project.port.input.UserUseCase;
import com.Account.Management.Project.port.output.AccountRepositoryPort;
import com.Account.Management.Project.port.output.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 gere les opérations sur les utilisateurs.
 * Implémente le port d'entrée UserUseCase.
 * Utilise les ports de sortie pour la persistance.
 */
@Service
@Transactional
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepository;
    private final AccountRepositoryPort accountRepository;

    // Injection de dépendances via constructeur
    public UserService(UserRepositoryPort userRepository,
                       AccountRepositoryPort accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Enregistre un nouvel utilisateur avec création automatique d'un compte bancaire.
     * C'est une opération transactionnelle qui crée à la fois l'utilisateur et son compte.
     */
    @Override
    public User registerUser(String firstName, String lastName, String email,
                             String password, Role role, String currencyCode) {
        // Vérifier que l'email n'existe pas déjà
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Un utilisateur avec cet email existe déjà: " + email);
        }

        // Créer l'utilisateur
        User user = new User(firstName, lastName, email, password, role);
        User savedUser = userRepository.save(user);

        // Créer automatiquement un compte bancaire associé
        Currency currency = Currency.fromCode(currencyCode);
        BankAccount account = new BankAccount(savedUser.getId(), currency);
        accountRepository.save(account);

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Bloque un utilisateur. Un utilisateur bloqué ne peut plus effectuer
     * d'opérations bancaires (transferts, retraits, etc.)
     */
    @Override
    public User blockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.block();
        return userRepository.save(user);
    }

    /**
     * Débloque un utilisateur, lui permettant de reprendre ses opérations.
     */
    @Override
    public User unblockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.unblock();
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID userId, String firstName, String lastName, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Vérifier si le nouvel email n'est pas déjà pris par un autre utilisateur
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
    }
}