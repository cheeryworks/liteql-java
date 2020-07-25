package org.cheeryworks.liteql.spring.context;

import org.cheeryworks.liteql.query.event.AbstractListMapQueryEvent;
import org.cheeryworks.liteql.service.query.QueryEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

public class SpringQueryEventPublisher implements QueryEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    public SpringQueryEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(AbstractListMapQueryEvent queryEvent) {
        this.applicationEventPublisher.publishEvent(queryEvent);
    }

}
