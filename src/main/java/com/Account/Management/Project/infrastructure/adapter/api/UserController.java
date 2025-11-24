package com.Account.Management.Project.infrastructure.adapter.api;


import com.Account.Management.Project.domain.model.Role;
import com.Account.Management.Project.domain.model.User;
import com.Account.Management.Project.port.input.UserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**les opérations sur les utilisateurs.
 * Adapter INPUT qui traduit les requêtes HTTP en appels au port d'entrée UserUseCase.
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*") // À configurer selon les besoins de sécurité
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    // dto

    /**
     * DTO pour la création d'un utilisateur
     */
    public record CreateUserRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            String role,           // "CUSTOMER" ou "MANAGER"
            String currencyCode    // "EUR", "USD", etc.
    ) {}

    /**
     * DTO pour la mise à jour d'un utilisateur
     */
    public record UpdateUserRequest(
            String firstName,
            String lastName,
            String email
    ) {}

    /**
     * DTO pour la réponse utilisateur (sans mot de passe)
     */
    public record UserResponse(
            UUID id,
            String firstName,
            String lastName,
            String email,
            String role,
            boolean blocked,
            String createdAt
    ) {
        public static UserResponse fromDomain(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.isBlocked(),
                    user.getCreatedAt().toString()
            );
        }
    }

    // endpoints

    /**
     * POST /api/v1/users - Créer un nouvel utilisateur
     * Crée automatiquement un compte bancaire associé
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        Role role = Role.valueOf(request.role().toUpperCase());

        User user = userUseCase.registerUser(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                role,
                request.currencyCode()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.fromDomain(user));
    }

    /**
     * GET /api/v1/users - Récupérer tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userUseCase.getAllUsers()
                .stream()
                .map(UserResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/{id} - Récupérer un utilisateur par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userUseCase.getUserById(id)
                .map(user -> ResponseEntity.ok(UserResponse.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/users/email/{email} - Récupérer un utilisateur par email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return userUseCase.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(UserResponse.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/v1/users/{id} - Mettre à jour un utilisateur
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {
        User user = userUseCase.updateUser(
                id,
                request.firstName(),
                request.lastName(),
                request.email()
        );

        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    /**
     * POST /api/v1/users/{id}/block - Bloquer un utilisateur
     */
    @PostMapping("/{id}/block")
    public ResponseEntity<UserResponse> blockUser(@PathVariable UUID id) {
        User user = userUseCase.blockUser(id);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    /**
     * POST /api/v1/users/{id}/unblock - Débloquer un utilisateur
     */
    @PostMapping("/{id}/unblock")
    public ResponseEntity<UserResponse> unblockUser(@PathVariable UUID id) {
        User user = userUseCase.unblockUser(id);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    /**
     * DELETE /api/v1/users/{id} - Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}