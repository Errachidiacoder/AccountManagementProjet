package com.Account.Management.Project.infrastructure.adapter.persistence.mongodb;

import com.Account.Management.Project.infrastructure.adapter.persistence.mongodb.entity.TransactionDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Spring Data MongoDB pour les transactions
 */
@Repository
public interface MongoTransactionRepository extends MongoRepository<TransactionDocument, String> {

    /**
     * Trouve les transactions où le compte est source ou destination
     */
    @Query("{ $or: [ { 'sourceAccountId': ?0 }, { 'targetAccountId': ?0 } ] }")
    List<TransactionDocument> findByAccountId(String accountId);

    /**
     * Trouve les transactions pour un compte dans une période
     */
    @Query("{ $and: [ " +
            "  { $or: [ { 'sourceAccountId': ?0 }, { 'targetAccountId': ?0 } ] }, " +
            "  { 'createdAt': { $gte: ?1, $lte: ?2 } } " +
            "] }")
    List<TransactionDocument> findByAccountIdAndPeriod(
            String accountId, LocalDateTime start, LocalDateTime end);

    List<TransactionDocument> findBySourceAccountId(String accountId);

    List<TransactionDocument> findByTargetAccountId(String accountId);

    /**
     * Trouve les transactions récentes avec pagination
     */
    @Query("{ $or: [ { 'sourceAccountId': ?0 }, { 'targetAccountId': ?0 } ] }")
    List<TransactionDocument> findRecentByAccountId(String accountId, Pageable pageable);

    long countBySourceAccountIdOrTargetAccountId(String sourceId, String targetId);
}