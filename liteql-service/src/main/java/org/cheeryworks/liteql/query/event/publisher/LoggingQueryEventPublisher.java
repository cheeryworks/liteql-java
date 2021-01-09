package org.cheeryworks.liteql.query.event.publisher;

import org.cheeryworks.liteql.query.event.QueryEvent;
import org.cheeryworks.liteql.util.LiteQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingQueryEventPublisher implements QueryEventPublisher {

    private Logger logger = LoggerFactory.getLogger(LoggingQueryEventPublisher.class);

    @Override
    public void publish(QueryEvent queryEvent) {
        logger.info(LiteQL.JacksonJsonUtils.toJson(queryEvent));
    }

}
