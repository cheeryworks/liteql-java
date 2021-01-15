package org.cheeryworks.liteql.skeleton.service.schema.migration;

import org.cheeryworks.liteql.skeleton.schema.migration.event.AbstractMigrationEvent;

public interface MigrationEventPublisher {

    void publish(AbstractMigrationEvent migrationEvent);

}
