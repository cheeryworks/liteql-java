package org.cheeryworks.liteql.jooq.service.query;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.event.publisher.query.QueryEventPublisher;
import org.cheeryworks.liteql.skeleton.event.publisher.query.QueryPublisher;
import org.cheeryworks.liteql.skeleton.service.query.QueryAuditingService;
import org.cheeryworks.liteql.skeleton.service.query.sql.AbstractSqlQueryService;

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
