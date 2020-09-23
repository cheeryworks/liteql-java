package org.cheeryworks.liteql.service.schema.migration;

import org.cheeryworks.liteql.schema.migration.event.AbstractMigrationEvent;

public interface MigrationEventPublisher {

    void publish(AbstractMigrationEvent migrationEvent);

}
