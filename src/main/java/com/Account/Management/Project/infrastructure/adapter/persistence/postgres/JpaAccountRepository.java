package com.Account.Management.Project.infrastructure.adapter.persistence.postgres;


import com.Account.Management.Project.infrastructure.adapter.persistence.postgres.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour les comptes bancaires
 */
@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    List<AccountEntity> findByUserId(UUID userId);

    boolean existsByAccountNumber(String accountNumber);
}