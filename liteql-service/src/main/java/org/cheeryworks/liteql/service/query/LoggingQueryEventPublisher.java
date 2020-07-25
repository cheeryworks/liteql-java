package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.event.AbstractListMapQueryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingQueryEventPublisher implements QueryEventPublisher {

    private Logger logger = LoggerFactory.getLogger(LoggingQueryEventPublisher.class);

    @Override
    public void publish(AbstractListMapQueryEvent queryEvent) {
        logger.info(queryEvent.toString());
    }

}
