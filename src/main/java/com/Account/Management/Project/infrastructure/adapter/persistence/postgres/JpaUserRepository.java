package com.Account.Management.Project.infrastructure.adapter.persistence.postgres;

import com.Account.Management.Project.infrastructure.adapter.persistence.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour les utilisateurs
 * Interface interne utilisée par l'adapter
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.blocked = true")
    List<UserEntity> findAllBlocked();
}