package org.cheeryworks.liteql.spring.context;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.query.event.QueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.publisher.QueryEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Sinks;

public class SpringQueryEventPublisher implements QueryEventPublisher {

    private LiteQLProperties liteQLProperties;

    private ApplicationEventPublisher applicationEventPublisher;

    private Sinks.Many<QueryEvent> queryEventSinksMany;

    public SpringQueryEventPublisher(
            LiteQLProperties liteQLProperties, ApplicationEventPublisher applicationEventPublisher,
            Sinks.Many<QueryEvent> queryEventSinksMany) {
        this.liteQLProperties = liteQLProperties;
        this.applicationEventPublisher = applicationEventPublisher;
        this.queryEventSinksMany = queryEventSinksMany;
    }

    @Override
    public void publish(QueryEvent queryEvent) {
        if (this.liteQLProperties.isMessagingEnabled() && this.queryEventSinksMany != null) {
            this.queryEventSinksMany.emitNext(queryEvent, (signalType, emitResult) -> false);
        } else {
            this.applicationEventPublisher.publishEvent(queryEvent);
        }
    }

}
