package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.service.QueryConditionNormalizer;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.cheeryworks.liteql.service.query.AbstractSqlQueryService;
import org.cheeryworks.liteql.service.query.AuditingService;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class JooqSqlQueryService extends AbstractSqlQueryService {

    public JooqSqlQueryService(
            Repository repository,
            DSLContext dslContext,
            SqlCustomizer sqlCustomizer,
            AuditingService auditingService,
            ApplicationEventPublisher applicationEventPublisher,
            List<QueryConditionNormalizer> queryConditionNormalizers) {
        super(
                repository,
                new JooqSqlQueryParser(repository, dslContext, sqlCustomizer),
                new JooqSqlQueryExecutor(dslContext, sqlCustomizer),
                auditingService,
                applicationEventPublisher,
                queryConditionNormalizers);
    }

}
