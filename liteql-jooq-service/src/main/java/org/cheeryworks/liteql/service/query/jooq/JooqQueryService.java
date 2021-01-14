package org.cheeryworks.liteql.service.query.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.query.QueryAuditingService;
import org.cheeryworks.liteql.query.event.publisher.QueryEventPublisher;
import org.cheeryworks.liteql.query.event.publisher.QueryPublisher;
import org.cheeryworks.liteql.service.query.sql.AbstractSqlQueryService;

public class JooqQueryService extends AbstractSqlQueryService {

    public JooqQueryService(
            LiteQLProperties liteQLProperties,
            JooqQueryParser jooqQueryParser,
            JooqQueryExecutor jooqQueryExecutor,
            QueryAuditingService queryAuditingService,
            QueryPublisher queryPublisher,
            QueryEventPublisher queryEventPublisher) {
        super(
                liteQLProperties,
                jooqQueryParser,
                jooqQueryExecutor,
                queryAuditingService,
                queryPublisher,
                queryEventPublisher);
    }

}
