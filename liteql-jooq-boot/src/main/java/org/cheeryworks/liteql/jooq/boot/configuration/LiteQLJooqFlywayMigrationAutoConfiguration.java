package org.cheeryworks.liteql.jooq.boot.configuration;


import org.cheeryworks.liteql.jooq.component.SpringJooqFlywayMigrationTransactionController;
import org.cheeryworks.liteql.jooq.event.listener.BeforeMigrationEventListenerForFlywayMigration;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrationTransactionController;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrator;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.internal.DefaultJooqFlywayMigrator;
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
    public JooqFlywayMigrationTransactionController springJdbcWithJOOQMigrationTransactionController() {
        return new SpringJooqFlywayMigrationTransactionController();
    }

    @Bean
    public JooqFlywayMigrator databaseMigrator(
            DataSource dataSource, DSLContext dslContext,
            JooqFlywayMigrationTransactionController transactionController) {
        JooqFlywayMigrator jooqFlywayMigrator = new DefaultJooqFlywayMigrator(
                dataSource, dslContext, transactionController);

        return jooqFlywayMigrator;
    }

    @Bean
    public BeforeMigrationEventListenerForFlywayMigration liteQLBeforeMigrationEventListenerForJooqMigration(
            LiteQLJooqFlywayMigrationProperties liteQLJooqFlywayMigrationProperties,
            JooqFlywayMigrator jooqFlywayMigrator) {
        return new BeforeMigrationEventListenerForFlywayMigration(
                liteQLJooqFlywayMigrationProperties, jooqFlywayMigrator);
    }

}
