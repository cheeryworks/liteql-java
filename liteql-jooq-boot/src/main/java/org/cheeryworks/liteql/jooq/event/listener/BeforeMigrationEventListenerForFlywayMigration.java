package org.cheeryworks.liteql.jooq.event.listener;

import org.cheeryworks.liteql.jooq.boot.configuration.LiteQLJooqProperties;
import org.cheeryworks.liteql.skeleton.schema.migration.event.BeforeMigrationEvent;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqDatabaseMigrator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;

public class BeforeMigrationEventListenerForFlywayMigration
        implements ApplicationListener<PayloadApplicationEvent<BeforeMigrationEvent>> {

    private LiteQLJooqProperties liteQLJooqProperties;

    private JooqDatabaseMigrator jooqDatabaseMigrator;

    public BeforeMigrationEventListenerForFlywayMigration(
            LiteQLJooqProperties liteQLJooqProperties, JooqDatabaseMigrator jooqDatabaseMigrator) {
        this.liteQLJooqProperties = liteQLJooqProperties;
        this.jooqDatabaseMigrator = jooqDatabaseMigrator;
    }

    @Override
    public void onApplicationEvent(PayloadApplicationEvent<BeforeMigrationEvent> event) {
        if (this.liteQLJooqProperties.isCleanBeforeMigration()) {
            this.jooqDatabaseMigrator.clean();
        }

        if (this.liteQLJooqProperties.isMigrationEnabled()) {
            this.jooqDatabaseMigrator.migrate();
        }
    }

}
