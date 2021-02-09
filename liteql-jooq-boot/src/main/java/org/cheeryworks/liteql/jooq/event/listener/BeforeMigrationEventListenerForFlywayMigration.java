package org.cheeryworks.liteql.jooq.event.listener;

import org.cheeryworks.liteql.jooq.boot.configuration.LiteQLJooqFlywayMigrationProperties;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrator;
import org.cheeryworks.liteql.skeleton.schema.migration.event.BeforeMigrationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;

public class BeforeMigrationEventListenerForFlywayMigration
        implements ApplicationListener<PayloadApplicationEvent<BeforeMigrationEvent>> {

    private LiteQLJooqFlywayMigrationProperties liteQLJooqFlywayMigrationProperties;

    private JooqFlywayMigrator jooqFlywayMigrator;

    public BeforeMigrationEventListenerForFlywayMigration(
            LiteQLJooqFlywayMigrationProperties liteQLJooqFlywayMigrationProperties,
            JooqFlywayMigrator jooqFlywayMigrator) {
        this.liteQLJooqFlywayMigrationProperties = liteQLJooqFlywayMigrationProperties;
        this.jooqFlywayMigrator = jooqFlywayMigrator;
    }

    @Override
    public void onApplicationEvent(PayloadApplicationEvent<BeforeMigrationEvent> event) {
        if (this.liteQLJooqFlywayMigrationProperties.isCleanBeforeMigration()) {
            this.jooqFlywayMigrator.clean();
        }

        this.jooqFlywayMigrator.migrate();
    }

}
