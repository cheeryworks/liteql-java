package org.cheeryworks.liteql.skeleton.event.publisher.schema.migration;

import org.cheeryworks.liteql.skeleton.schema.migration.event.MigrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingMigrationEventPublisher implements MigrationEventPublisher {

    private Logger logger = LoggerFactory.getLogger(LoggingMigrationEventPublisher.class);

    @Override
    public void publish(MigrationEvent migrationEvent) {
        logger.info("MigrationEvent [" + migrationEvent.getClass().getSimpleName() + "] triggered.");
    }

}
