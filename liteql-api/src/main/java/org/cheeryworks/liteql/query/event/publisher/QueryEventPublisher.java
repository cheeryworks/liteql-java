package org.cheeryworks.liteql.query.event.publisher;

import org.cheeryworks.liteql.query.event.QueryEvent;

public interface QueryEventPublisher {

    void publish(QueryEvent queryEvent);

}
