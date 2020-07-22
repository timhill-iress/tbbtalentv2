/*
 * Copyright (c) 2020 Talent Beyond Boundaries. All rights reserved.
 */

package org.tbbtalent.server.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Based on https://springframework.guru/how-to-configure-multiple-data-sources-in-a-spring-boot-application/ 
 *
 * @author John Cameron
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "org.tbbtalent.server.repository.db",
        entityManagerFactoryRef = "dbEntityManagerFactory",
        transactionManagerRef= "dbTransactionManager"    
)
public class DatabaseConfiguration {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dbDataSourceProperties() {
        return new DataSourceProperties();
    }
    
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.configuration")
    public DataSource dbDataSource() {
        DataSourceProperties props = dbDataSourceProperties(); 
        return dbDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "dbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean 
    dbEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        
        //Note that this is necessary because with multiple data sources the 
        //default naming strategies are not picked up.
        //If you don't do this database fields like first_name no longer map to 
        //entity names like firstName.
        //See https://stackoverflow.com/questions/40509395/cant-set-jpa-naming-strategy-after-configuring-multiple-data-sources-spring-1 
        Map<String, String> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        jpaProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());

        return builder
                .dataSource(dbDataSource())
                .packages(
                        "org.tbbtalent.server.service.db", 
                        "org.tbbtalent.server.model.db"
                )
                .properties(jpaProperties)
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager dbTransactionManager(
            final @Qualifier("dbEntityManagerFactory") 
                    LocalContainerEntityManagerFactoryBean 
                    dbEntityManagerFactory) {
        return new JpaTransactionManager(dbEntityManagerFactory.getObject());
    }
    
}