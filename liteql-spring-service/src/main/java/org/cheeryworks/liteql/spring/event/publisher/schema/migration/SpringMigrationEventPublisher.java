package org.cheeryworks.liteql.spring.event.publisher.schema.migration;

import org.cheeryworks.liteql.skeleton.event.publisher.schema.migration.MigrationEventPublisher;
import org.cheeryworks.liteql.skeleton.schema.migration.event.MigrationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class SpringMigrationEventPublisher implements MigrationEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    public SpringMigrationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(MigrationEvent migrationEvent) {
        this.applicationEventPublisher.publishEvent(migrationEvent);
    }

}
