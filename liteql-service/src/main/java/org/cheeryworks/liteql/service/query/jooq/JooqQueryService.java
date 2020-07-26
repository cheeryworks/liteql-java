package org.cheeryworks.liteql.service.query.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.service.query.QueryAuditingService;
import org.cheeryworks.liteql.service.query.QueryEventPublisher;
import org.cheeryworks.liteql.service.query.sql.AbstractSqlQueryService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.DSLContext;

public class JooqQueryService extends AbstractSqlQueryService {

    public JooqQueryService(
            LiteQLProperties liteQLProperties,
            SchemaService schemaService,
            DSLContext dslContext,
            SqlCustomizer sqlCustomizer,
            QueryAuditingService queryAuditingService,
            QueryAccessDecisionService queryAccessDecisionService,
            QueryEventPublisher queryEventPublisher) {
        super(
                liteQLProperties,
                schemaService,
                new JooqQueryParser(liteQLProperties, schemaService, dslContext, sqlCustomizer),
                new JooqQueryExecutor(liteQLProperties, dslContext),
                queryAuditingService,
                queryAccessDecisionService,
                queryEventPublisher);
    }

}
