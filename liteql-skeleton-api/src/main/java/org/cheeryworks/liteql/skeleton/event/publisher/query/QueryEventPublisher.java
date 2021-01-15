package org.cheeryworks.liteql.skeleton.event.publisher.query;

import org.cheeryworks.liteql.skeleton.query.event.QueryEvent;

public interface QueryEventPublisher {

    void publish(QueryEvent queryEvent);

}
