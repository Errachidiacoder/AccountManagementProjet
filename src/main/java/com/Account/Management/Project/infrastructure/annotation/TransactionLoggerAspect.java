package com.Account.Management.Project.infrastructure.annotation;


import com.Account.Management.Project.domain.model.Transaction;
import com.Account.Management.Project.infrastructure.adapter.persistence.mongodb.MongoTransactionRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 * Aspect AOP pour capturer et logger automatiquement les transactions bancaires.
 *
 * Cet aspect intercepte toutes les méthodes annotées avec @LogTransaction
 * et effectue les opérations suivantes:
 *
 * 1. avant l'exécution: Log des paramètres d'entrée
 * 2. apres l'exécution: Log du résultat et stockage dans MongoDB
 * 3. en cas d'erreurs: Log de l'exception et informations de debug
 *
 * L'aspect utilise @Around pour avoir un contrôle complet sur l'exécution
 * et mesurer le temps d'exécution.
 */
@Aspect
@Component
public class TransactionLoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(TransactionLoggerAspect.class);

    private final MongoTransactionRepository mongoRepository;

    public TransactionLoggerAspect(MongoTransactionRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    /**
     * Point de coupe (Pointcut) ciblant toutes les méthodes annotées avec @LogTransaction
     */
    @Pointcut("@annotation(logTransaction)")
    public void transactionMethods(LogTransaction logTransaction) {
        // Point de coupe pour les méthodes annotées
    }

    /**
     * Advice @Around - Intercepte l'exécution complète de la méthode.
     *
     * Permet de:
     * - Capturer les paramètres avant l'exécution
     * - Mesurer le temps d'exécution
     * - Capturer le résultat ou l'exception
     * - Logger dans MongoDB
     */
    @Around("transactionMethods(logTransaction)")
    public Object logTransactionExecution(ProceedingJoinPoint joinPoint,
                                          LogTransaction logTransaction) throws Throwable {

        // Récupérer les informations sur la méthode
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        // Générer un ID de corrélation pour tracer cette opération
        String correlationId = UUID.randomUUID().toString().substring(0, 8);

        // Log de début d'exécution
        logger.info("[{}] DÉBUT Transaction - {}.{}",
                correlationId, className, methodName);
        logger.debug("[{}] Paramètres: {}",
                correlationId, formatParameters(paramNames, args));

        // Mesurer le temps d'exécution
        long startTime = System.currentTimeMillis();

        try {
            // Exécuter la méthode originale
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // Logger le succès
            logger.info("[{}] SUCCÈS Transaction - {}.{} - Temps: {}ms",
                    correlationId, className, methodName, executionTime);

            // Si le résultat est une Transaction, logger les détails
            if (result instanceof Transaction transaction) {
                logTransactionDetails(correlationId, transaction);

                // Stocker dans MongoDB via l'aspect (si pas déjà fait par le service)
                // Note: Normalement la transaction est déjà sauvegardée par le service
                // Ici on ajoute des métadonnées de logging
                enrichAndSaveTransactionLog(transaction, executionTime, correlationId);
            }

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            // Logger l'échec
            logger.error("[{}] ÉCHEC Transaction - {}.{} - Temps: {}ms - Erreur: {}",
                    correlationId, className, methodName, executionTime, e.getMessage());

            // Si configuré pour logger les échecs, créer un log d'erreur
            if (logTransaction.logOnFailure()) {
                logFailedTransaction(correlationId, methodName, args, e, executionTime);
            }

            // Relancer l'exception
            throw e;
        }
    }

    /**
     * Advice @Before - Exécuté avant la méthode (optionnel, pour logging supplémentaire)
     */
    @Before("transactionMethods(logTransaction)")
    public void beforeTransaction(JoinPoint joinPoint, LogTransaction logTransaction) {
        String description = logTransaction.description();
        if (!description.isEmpty()) {
            logger.debug("Démarrage transaction: {}", description);
        }
    }

    /**
     * Advice @AfterReturning - Exécuté après un retour réussi
     */
    @AfterReturning(
            pointcut = "transactionMethods(logTransaction)",
            returning = "result"
    )
    public void afterSuccessfulTransaction(JoinPoint joinPoint,
                                           LogTransaction logTransaction,
                                           Object result) {
        if (result instanceof Transaction tx) {
            logger.info("Transaction {} complétée avec succès - Type: {} - Montant: {}",
                    tx.getId(), tx.getType(), tx.getSourceAmount());
        }
    }

    /**
     * Advice @AfterThrowing - Exécuté en cas d'exception
     */
    @AfterThrowing(
            pointcut = "transactionMethods(logTransaction)",
            throwing = "exception"
    )
    public void afterFailedTransaction(JoinPoint joinPoint,
                                       LogTransaction logTransaction,
                                       Exception exception) {
        logger.error("Transaction échouée: {} - {}",
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    // méthodes utilitaires privées

    /**
     * Formate les paramètres pour le logging
     */
    private String formatParameters(String[] paramNames, Object[] args) {
        if (paramNames == null || args == null) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(paramNames[i]).append("=");

            // Masquer les données sensibles si nécessaire
            if (paramNames[i].toLowerCase().contains("password")) {
                sb.append("***");
            } else {
                sb.append(args[i]);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Log les détails d'une transaction réussie
     */
    private void logTransactionDetails(String correlationId, Transaction transaction) {
        logger.info("[{}] Détails Transaction:", correlationId);
        logger.info("  - ID: {}", transaction.getId());
        logger.info("  - Type: {}", transaction.getType());
        logger.info("  - Source: {}", transaction.getSourceAccountId());
        logger.info("  - Destination: {}", transaction.getTargetAccountId());
        logger.info("  - Montant source: {}", transaction.getSourceAmount());
        logger.info("  - Montant cible: {}", transaction.getTargetAmount());
        if (transaction.isForex()) {
            logger.info("  - Taux de change: {}", transaction.getExchangeRate());
        }
        logger.info("  - Statut: {}", transaction.getStatus());
    }

    /**
     * Enrichit la transaction avec des métadonnées et la sauvegarde
     * Note: Dans une implémentation réelle, on pourrait avoir une collection
     * séparée pour les logs d'audit
     */
    private void enrichAndSaveTransactionLog(Transaction transaction,
                                             long executionTime,
                                             String correlationId) {
        // La transaction est déjà sauvegardée par le service
        // Ici on pourrait ajouter des métadonnées d'audit dans une collection séparée
        logger.debug("[{}] Transaction {} sauvegardée - Temps d'exécution: {}ms",
                correlationId, transaction.getId(), executionTime);
    }

    /**
     * Log une transaction échouée
     */
    private void logFailedTransaction(String correlationId,
                                      String methodName,
                                      Object[] args,
                                      Exception e,
                                      long executionTime) {
        logger.error("[{}] Échec de {} - Paramètres: {} - Exception: {} - Temps: {}ms",
                correlationId,
                methodName,
                Arrays.toString(args),
                e.getMessage(),
                executionTime);

        // On pourrait aussi stocker les échecs dans MongoDB pour audit
        // Par exemple dans une collection "failed_transactions"
    }
}