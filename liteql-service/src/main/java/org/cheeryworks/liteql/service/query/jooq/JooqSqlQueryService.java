package org.cheeryworks.liteql.service.query.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.auditing.AuditingService;
import org.cheeryworks.liteql.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.service.query.sql.AbstractSqlQueryService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationEventPublisher;

public class JooqSqlQueryService extends AbstractSqlQueryService {

    public JooqSqlQueryService(
            LiteQLProperties liteQLProperties,
            SchemaService schemaService,
            DSLContext dslContext,
            SqlCustomizer sqlCustomizer,
            AuditingService auditingService,
            QueryAccessDecisionService queryAccessDecisionService,
            ApplicationEventPublisher applicationEventPublisher) {
        super(
                liteQLProperties,
                schemaService,
                new JooqSqlQueryParser(liteQLProperties, schemaService, dslContext, sqlCustomizer),
                new JooqSqlQueryExecutor(liteQLProperties, dslContext, sqlCustomizer),
                sqlCustomizer,
                auditingService,
                queryAccessDecisionService,
                applicationEventPublisher);
    }

}
