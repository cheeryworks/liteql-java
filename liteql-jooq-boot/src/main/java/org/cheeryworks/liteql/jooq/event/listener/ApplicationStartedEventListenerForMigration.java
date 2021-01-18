package org.cheeryworks.liteql.jooq.event.listener;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.event.ApplicationStartedEvent;
import org.cheeryworks.liteql.skeleton.service.schema.migration.MigrationService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;

public class ApplicationStartedEventListenerForMigration
        implements ApplicationListener<PayloadApplicationEvent<ApplicationStartedEvent>> {

    private LiteQLProperties liteQLProperties;

    private MigrationService migrationService;

    public ApplicationStartedEventListenerForMigration(
            LiteQLProperties liteQLProperties, MigrationService migrationService) {
        this.liteQLProperties = liteQLProperties;
        this.migrationService = migrationService;
    }

    @Override
    public void onApplicationEvent(PayloadApplicationEvent<ApplicationStartedEvent> event) {
        if (liteQLProperties.isMigrationEnabled()) {
            this.migrationService.migrate();
        }
    }

}
