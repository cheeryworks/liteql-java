package org.cheeryworks.liteql.skeleton.query.event.publisher;

import org.cheeryworks.liteql.skeleton.query.PublicQuery;

public interface QueryPublisher {

    void publish(PublicQuery publicQuery);

}
