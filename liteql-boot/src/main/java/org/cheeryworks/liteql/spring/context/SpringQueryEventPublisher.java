package org.cheeryworks.liteql.spring.context;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.query.event.QueryEvent;
import org.cheeryworks.liteql.service.query.QueryEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Sinks;

public class SpringQueryEventPublisher implements QueryEventPublisher {

    private LiteQLProperties liteQLProperties;

    private ApplicationEventPublisher applicationEventPublisher;

    private Sinks.Many<QueryEvent> queryEventSinksMany;

    private boolean initialized = false;

    public SpringQueryEventPublisher(
            LiteQLProperties liteQLProperties, ApplicationEventPublisher applicationEventPublisher,
            Sinks.Many<QueryEvent> queryEventSinksMany) {
        this.liteQLProperties = liteQLProperties;
        this.applicationEventPublisher = applicationEventPublisher;
        this.queryEventSinksMany = queryEventSinksMany;
    }

    @Override
    public void publish(QueryEvent queryEvent) {
        this.applicationEventPublisher.publishEvent(queryEvent);

        if (this.liteQLProperties.isMessagingEnabled() && this.queryEventSinksMany != null) {
            this.queryEventSinksMany.emitNext(queryEvent, (signalType, emitResult) -> false);
        }
    }

}
