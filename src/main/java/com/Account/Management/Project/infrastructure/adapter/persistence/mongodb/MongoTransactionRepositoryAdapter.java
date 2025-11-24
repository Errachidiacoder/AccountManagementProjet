package com.Account.Management.Project.infrastructure.adapter.persistence.mongodb;

import com.Account.Management.Project.domain.model.Transaction;
import com.Account.Management.Project.infrastructure.adapter.persistence.mongodb.entity.TransactionDocument;
import com.Account.Management.Project.port.output.TransactionRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implémentant le port de sortie TransactionRepositoryPort
 * Gère la persistance des transactions dans MongoDB
 *
 * MongoDB est choisi pour les transactions car:
 * - Haute performance en lecture pour l'historique
 * - Scalabilité horizontale (sharding)
 * - Schéma flexible pour les métadonnées de transaction
 */
@Component
public class MongoTransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final MongoTransactionRepository mongoRepository;

    public MongoTransactionRepositoryAdapter(MongoTransactionRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionDocument document = TransactionDocument.fromDomain(transaction);
        TransactionDocument savedDocument = mongoRepository.save(document);
        return savedDocument.toDomain();
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return mongoRepository.findById(id)
                .map(TransactionDocument::toDomain);
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        String accountIdStr = accountId.toString();
        return mongoRepository.findByAccountId(accountIdStr)
                .stream()
                .map(TransactionDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByAccountIdAndPeriod(UUID accountId,
                                                      LocalDateTime start,
                                                      LocalDateTime end) {
        String accountIdStr = accountId.toString();
        return mongoRepository.findByAccountIdAndPeriod(accountIdStr, start, end)
                .stream()
                .map(TransactionDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findBySourceAccountId(UUID accountId) {
        return mongoRepository.findBySourceAccountId(accountId.toString())
                .stream()
                .map(TransactionDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByTargetAccountId(UUID accountId) {
        return mongoRepository.findByTargetAccountId(accountId.toString())
                .stream()
                .map(TransactionDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findRecentByAccountId(UUID accountId, int limit) {
        String accountIdStr = accountId.toString();
        // Pagination avec tri par date décroissante
        PageRequest pageRequest = PageRequest.of(0, limit,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoRepository.findRecentByAccountId(accountIdStr, pageRequest)
                .stream()
                .map(TransactionDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByAccountId(UUID accountId) {
        String accountIdStr = accountId.toString();
        return mongoRepository.countBySourceAccountIdOrTargetAccountId(
                accountIdStr, accountIdStr);
    }
}