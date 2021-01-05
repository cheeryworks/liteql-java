package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.event.QueryEvent;

public interface QueryEventPublisher {

    void publish(QueryEvent queryEvent);

}
