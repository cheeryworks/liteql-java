package org.cheeryworks.liteql.skeleton.event.publisher.schema.migration;

import org.cheeryworks.liteql.skeleton.schema.migration.event.MigrationEvent;

public interface MigrationEventPublisher {

    void publish(MigrationEvent migrationEvent);

}
