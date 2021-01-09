package org.cheeryworks.liteql.query.event.publisher;

import org.cheeryworks.liteql.query.PublicQuery;

public interface QueryPublisher {

    void publish(PublicQuery publicQuery);

}
