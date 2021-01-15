package org.cheeryworks.liteql.skeleton.query.event.publisher;

import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingQueryPublisher implements QueryPublisher {

    private Logger logger = LoggerFactory.getLogger(LoggingQueryEventPublisher.class);

    @Override
    public void publish(PublicQuery publicQuery) {
        logger.info(LiteQL.JacksonJsonUtils.toJson(publicQuery));
    }
}
