package com.Account.Management.Project.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation personnalisée pour marquer les méthodes qui effectuent des transactions
 * L'aspect capture:
 * - Les paramètres d'entrée (comptes source/destination, montant)
 * - Le résultat de la transaction
 * - Le temps d'exécution
 * - Les éventuelles erreurs
 */
@Target(ElementType.METHOD)        // Applicable uniquement aux méthodes
@Retention(RetentionPolicy.RUNTIME) // Disponible à l'exécution pour la réflexion
public @interface LogTransaction {

    /**
     * Description optionnelle de l'opération
     * Utilisé pour enrichir les logs
     */
    String description() default "";

    /**
     * Indique si l'aspect doit logger même en cas d'échec
     * Par défaut, les échecs sont également loggés
     */
    boolean logOnFailure() default true;
}