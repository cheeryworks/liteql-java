package org.cheeryworks.liteql.spring.event.publisher.schema.migration;

import org.cheeryworks.liteql.skeleton.schema.migration.event.AbstractMigrationEvent;
import org.cheeryworks.liteql.skeleton.event.publisher.schema.migration.MigrationEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

public class SpringMigrationEventPublisher implements MigrationEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    public SpringMigrationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(AbstractMigrationEvent migrationEvent) {
        this.applicationEventPublisher.publishEvent(migrationEvent);
    }

}
