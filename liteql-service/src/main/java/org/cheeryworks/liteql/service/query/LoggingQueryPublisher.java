package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.util.LiteQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingQueryPublisher implements QueryPublisher {

    private Logger logger = LoggerFactory.getLogger(LoggingQueryEventPublisher.class);

    @Override
    public void publish(PublicQuery publicQuery) {
        logger.info(LiteQL.JacksonJsonUtils.toJson(publicQuery));
    }
}
