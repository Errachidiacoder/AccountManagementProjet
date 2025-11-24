package com.Account.Management.Project.infrastructure.adapter.persistence.postgres;


import com.Account.Management.Project.domain.model.BankAccount;
import com.Account.Management.Project.infrastructure.adapter.persistence.postgres.entity.AccountEntity;
import com.Account.Management.Project.port.output.AccountRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implémentant le port de sortie AccountRepositoryPort
 * Gère la persistance des comptes bancaires dans PostgreSQL via Jpa
 */
@Component
public class JpaAccountRepositoryAdapter implements AccountRepositoryPort {

    private final JpaAccountRepository jpaRepository;

    public JpaAccountRepositoryAdapter(JpaAccountRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BankAccount save(BankAccount account) {
        AccountEntity entity = AccountEntity.fromDomain(account);
        AccountEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<BankAccount> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(AccountEntity::toDomain);
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber)
                .map(AccountEntity::toDomain);
    }

    @Override
    public List<BankAccount> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(AccountEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankAccount> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(AccountEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }
}
