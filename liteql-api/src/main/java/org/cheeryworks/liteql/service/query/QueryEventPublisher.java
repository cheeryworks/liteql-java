package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.event.AbstractListMapQueryEvent;

public interface QueryEventPublisher {

    void publish(AbstractListMapQueryEvent queryEvent);

}
