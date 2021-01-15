package org.cheeryworks.liteql.spring.event.publisher.query;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.event.publisher.query.QueryPublisher;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Sinks;

public class SpringQueryPublisher implements QueryPublisher {

    private LiteQLProperties liteQLProperties;

    private ApplicationEventPublisher applicationEventPublisher;

    private Sinks.Many<PublicQuery> querySinksMany;

    public SpringQueryPublisher(
            LiteQLProperties liteQLProperties, ApplicationEventPublisher applicationEventPublisher,
            Sinks.Many<PublicQuery> querySinksMany) {
        this.liteQLProperties = liteQLProperties;
        this.applicationEventPublisher = applicationEventPublisher;
        this.querySinksMany = querySinksMany;
    }

    @Override
    public void publish(PublicQuery publicQuery) {
        if (this.liteQLProperties.isMessagingEnabled() && this.querySinksMany != null) {
            this.querySinksMany.emitNext(publicQuery, (signalType, emitResult) -> false);
        } else {
            this.applicationEventPublisher.publishEvent(publicQuery);
        }
    }

}
