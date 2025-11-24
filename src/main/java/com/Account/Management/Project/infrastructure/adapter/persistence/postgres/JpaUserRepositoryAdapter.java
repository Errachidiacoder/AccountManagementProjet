package com.Account.Management.Project.infrastructure.adapter.persistence.postgres;


import com.Account.Management.Project.domain.model.User;
import com.Account.Management.Project.infrastructure.adapter.persistence.postgres.entity.UserEntity;
import com.Account.Management.Project.port.output.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implémentant le port de sortie UserRepositoryPort
 * Fait le pont entre le domaine et Spring Data JPA pour PostgreSql
 *
 * Cet adapter traduit les opérations du domaine en opérations Jpa
 * et convertit les entités JPA en objets du domaine
 */
@Component
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaRepository;

    public JpaUserRepositoryAdapter(JpaUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        // Convertir le domaine en entité JPA
        UserEntity entity = UserEntity.fromDomain(user);
        // Sauvegarder via Spring Data
        UserEntity savedEntity = jpaRepository.save(entity);
        // Reconvertir en domaine
        return savedEntity.toDomain();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAllBlocked() {
        return jpaRepository.findAllBlocked()
                .stream()
                .map(UserEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
