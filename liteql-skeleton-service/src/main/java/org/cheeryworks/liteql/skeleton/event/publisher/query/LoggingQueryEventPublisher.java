package org.cheeryworks.liteql.skeleton.event.publisher.query;

import org.cheeryworks.liteql.skeleton.query.event.QueryEvent;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingQueryEventPublisher implements QueryEventPublisher {

    private Logger logger = LoggerFactory.getLogger(LoggingQueryEventPublisher.class);

    @Override
    public void publish(QueryEvent queryEvent) {
        logger.info(LiteQL.JacksonJsonUtils.toJson(queryEvent));
    }

}
