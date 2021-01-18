package org.cheeryworks.liteql.jooq.event.listener;

import org.cheeryworks.liteql.jooq.boot.configuration.LiteQLJooqFlywayMigrationProperties;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqDatabaseMigrator;
import org.cheeryworks.liteql.skeleton.schema.migration.event.BeforeMigrationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;

public class BeforeMigrationEventListenerForFlywayMigration
        implements ApplicationListener<PayloadApplicationEvent<BeforeMigrationEvent>> {

    private LiteQLJooqFlywayMigrationProperties liteQLJooqFlywayMigrationProperties;

    private JooqDatabaseMigrator jooqDatabaseMigrator;

    public BeforeMigrationEventListenerForFlywayMigration(
            LiteQLJooqFlywayMigrationProperties liteQLJooqFlywayMigrationProperties,
            JooqDatabaseMigrator jooqDatabaseMigrator) {
        this.liteQLJooqFlywayMigrationProperties = liteQLJooqFlywayMigrationProperties;
        this.jooqDatabaseMigrator = jooqDatabaseMigrator;
    }

    @Override
    public void onApplicationEvent(PayloadApplicationEvent<BeforeMigrationEvent> event) {
        if (this.liteQLJooqFlywayMigrationProperties.isCleanBeforeMigration()) {
            this.jooqDatabaseMigrator.clean();
        }

        this.jooqDatabaseMigrator.migrate();
    }

}
