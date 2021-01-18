package org.cheeryworks.liteql.jooq.boot.configuration;


import org.cheeryworks.liteql.jooq.component.SpringJooqMigrationTransactionController;
import org.cheeryworks.liteql.jooq.event.listener.BeforeMigrationEventListenerForFlywayMigration;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqDatabaseMigrator;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqMigrationTransactionController;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.internal.DefaultJooqDatabaseMigrator;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Flyway.class, DSLContext.class})
@EnableConfigurationProperties({
        LiteQLJooqFlywayMigrationProperties.class
})
@ConditionalOnProperty(
        prefix = LiteQLJooqFlywayMigrationProperties.PREFIX,
        name = "enabled", matchIfMissing = true
)
public class LiteQLJooqFlywayMigrationAutoConfiguration {

    @Bean
    public JooqMigrationTransactionController springJdbcWithJOOQMigrationTransactionController() {
        return new SpringJooqMigrationTransactionController();
    }

    @Bean
    public JooqDatabaseMigrator databaseMigrator(
            DataSource dataSource, DSLContext dslContext, JooqMigrationTransactionController transactionController) {
        JooqDatabaseMigrator jooqDatabaseMigrator = new DefaultJooqDatabaseMigrator(
                dataSource, dslContext, transactionController);

        return jooqDatabaseMigrator;
    }

    @Bean
    public BeforeMigrationEventListenerForFlywayMigration liteQLBeforeMigrationEventListenerForJooqMigration(
            LiteQLJooqFlywayMigrationProperties liteQLJooqFlywayMigrationProperties,
            JooqDatabaseMigrator jooqDatabaseMigrator) {
        return new BeforeMigrationEventListenerForFlywayMigration(
                liteQLJooqFlywayMigrationProperties, jooqDatabaseMigrator);
    }

}
