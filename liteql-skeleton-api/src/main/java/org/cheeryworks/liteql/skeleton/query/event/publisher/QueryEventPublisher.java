package org.cheeryworks.liteql.skeleton.query.event.publisher;

import org.cheeryworks.liteql.skeleton.query.event.QueryEvent;

public interface QueryEventPublisher {

    void publish(QueryEvent queryEvent);

}
