package org.cheeryworks.liteql.service.query.jooq;

import org.cheeryworks.liteql.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.service.auditing.AuditingService;
import org.cheeryworks.liteql.service.query.sql.AbstractSqlQueryService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationEventPublisher;

public class JooqSqlQueryService extends AbstractSqlQueryService {

    public JooqSqlQueryService(
            Repository repository,
            DSLContext dslContext,
            SqlCustomizer sqlCustomizer,
            AuditingService auditingService,
            QueryAccessDecisionService queryAccessDecisionService,
            ApplicationEventPublisher applicationEventPublisher) {
        super(
                repository,
                new JooqSqlQueryParser(repository, dslContext, sqlCustomizer),
                new JooqSqlQueryExecutor(dslContext, sqlCustomizer),
                auditingService,
                queryAccessDecisionService,
                applicationEventPublisher);
    }

}
