package org.cheeryworks.liteql.service.query.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.service.query.QueryAuditingService;
import org.cheeryworks.liteql.service.query.QueryEventPublisher;
import org.cheeryworks.liteql.service.query.sql.AbstractSqlQueryService;

public class JooqQueryService extends AbstractSqlQueryService {

    public JooqQueryService(
            LiteQLProperties liteQLProperties,
            JooqQueryParser jooqQueryParser,
            JooqQueryExecutor jooqQueryExecutor,
            QueryAuditingService queryAuditingService,
            QueryAccessDecisionService queryAccessDecisionService,
            QueryEventPublisher queryEventPublisher) {
        super(
                liteQLProperties,
                jooqQueryParser,
                jooqQueryExecutor,
                queryAuditingService,
                queryAccessDecisionService,
                queryEventPublisher);
    }

}
