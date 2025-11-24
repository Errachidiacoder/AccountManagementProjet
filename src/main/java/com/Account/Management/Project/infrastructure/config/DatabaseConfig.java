package com.Account.Management.Project.infrastructure.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuration des bases de données:
 * - PostgreSQL pour les utilisateurs et comptes (Jpa/Hibernate)
 * - MongoDB pour les transactions (Spring Data MongoDB)
 *
 * Cette configuration active:
 * - Les repositories JPA pour le package postgres
 * - Les repositories MongoDB pour le package mongodb
 * - La gestion des transactions
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.Account.Management.Project.infrastructure.adapter.persistence.postgres",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@EnableMongoRepositories(
        basePackages = "com.Account.Management.Project.infrastructure.adapter.persistence.mongodb"
)
public class DatabaseConfig {

    //Configuration PostgreSql

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/bankdb}")
    private String postgresUrl;

    @Value("${spring.datasource.username:postgres}")
    private String postgresUsername;

    @Value("${spring.datasource.password:postgres}")
    private String postgresPassword;

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String hibernateDdlAuto;

    @Value("${spring.jpa.show-sql:true}")
    private boolean showSql;

    /**
     * Configure la source de données PostgreSQL
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(postgresUrl);
        dataSource.setUsername(postgresUsername);
        dataSource.setPassword(postgresPassword);
        return dataSource;
    }

    /**
     * Configure l'EntityManager pour JPA/Hibernate
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.Account.Management.Project.infrastructure.adapter.persistence.postgres.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(showSql);
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", hibernateDdlAuto);
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.format_sql", "true");
        em.setJpaProperties(properties);

        return em;
    }

    /**
     * Configure le gestionnaire de transactions JPA
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}