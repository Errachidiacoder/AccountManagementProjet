package com.Account.Management.Project.domain.model;
import java.time.LocalDateTime;
import java.util.UUID;

/**
  informations personnelles et l'état du compte utilisateur.
 */
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private boolean blocked;          // Indique si l'utilisateur est bloqué
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.blocked = false;
        this.role = Role.CUSTOMER;
    }


    public User(String firstName, String lastName, String email, String password, Role role) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Méthodes métier


    public void block() {
        this.blocked = true;
        this.updatedAt = LocalDateTime.now();
    }


    public void unblock() {
        this.blocked = false;
        this.updatedAt = LocalDateTime.now();
    }


    public boolean canPerformOperations() {
        return !blocked;
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}