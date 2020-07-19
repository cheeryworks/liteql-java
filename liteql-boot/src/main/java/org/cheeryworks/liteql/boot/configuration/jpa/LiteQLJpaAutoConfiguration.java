package org.cheeryworks.liteql.boot.configuration.jpa;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.jpa.JpaRepository;
import org.cheeryworks.liteql.jpa.JpaSqlCustomizer;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;

@Configuration(proxyBeanMethods = false)
public class LiteQLJpaAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(LiteQLJpaAutoConfiguration.class);

    @Bean
    public EntityManagerFactoryBuilderCustomizer databaseStatusChecker(DataSource dataSource) {
        return builder -> checkingDatabaseStatus(dataSource);
    }

    private void checkingDatabaseStatus(DataSource dataSource) {
        boolean ready = false;
        for (int i = 0; i < 30; i++) {
            try {
                Connection connection = null;
                try {
                    connection = dataSource.getConnection();

                    ready = true;

                    break;
                } catch (Exception ex) {
                    logger.info("Waiting for database ready...");
                } finally {
                    DataSourceUtils.releaseConnection(connection, dataSource);
                }

                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
        }

        if (!ready) {
            throw new IllegalStateException("Database not ready");
        }
    }

    @Bean
    public SqlCustomizer sqlCustomizer(EntityManagerFactory entityManagerFactory) {
        return new JpaSqlCustomizer(entityManagerFactory);
    }

    @Bean
    public Repository jpaRepository(ObjectMapper objectMapper, EntityManagerFactory entityManagerFactory) {
        return new JpaRepository(objectMapper, entityManagerFactory);
    }

}
