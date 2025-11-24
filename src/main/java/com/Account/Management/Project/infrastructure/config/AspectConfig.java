package com.Account.Management.Project.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration pour activer la programmation orientée aspect
 * Nécessaire pour que l'annotation @LogTransaction fonctionne
 */
@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {
    // L'annotation @EnableAspectJAutoProxy active le support AOP
    // Les aspects comme TransactionLoggerAspect seront automatiquement détectés
}
