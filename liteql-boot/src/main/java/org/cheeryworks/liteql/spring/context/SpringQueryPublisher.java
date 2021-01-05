package org.cheeryworks.liteql.spring.context;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.service.query.QueryPublisher;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Sinks;

public class SpringQueryPublisher implements QueryPublisher {

    private LiteQLProperties liteQLProperties;

    private ApplicationEventPublisher applicationEventPublisher;

    private Sinks.Many<PublicQuery> querySinksMany;

    private boolean initialized = false;

    public SpringQueryPublisher(
            LiteQLProperties liteQLProperties, ApplicationEventPublisher applicationEventPublisher,
            Sinks.Many<PublicQuery> querySinksMany) {
        this.liteQLProperties = liteQLProperties;
        this.applicationEventPublisher = applicationEventPublisher;
        this.querySinksMany = querySinksMany;
    }

    @Override
    public void publish(PublicQuery publicQuery) {
        this.applicationEventPublisher.publishEvent(publicQuery);

        if (this.liteQLProperties.isMessagingEnabled() && this.querySinksMany != null) {
            this.querySinksMany.emitNext(publicQuery, (signalType, emitResult) -> false);
        }
    }

}
