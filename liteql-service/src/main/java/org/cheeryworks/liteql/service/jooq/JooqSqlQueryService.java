package org.cheeryworks.liteql.service.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.cheeryworks.liteql.service.query.AbstractSqlQueryService;
import org.cheeryworks.liteql.service.query.AuditingService;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationEventPublisher;

public class JooqSqlQueryService extends AbstractSqlQueryService {

    public JooqSqlQueryService(
            Repository repository, ObjectMapper objectMapper,
            DSLContext dslContext, SqlCustomizer sqlCustomizer,
            AuditingService auditingService,
            ApplicationEventPublisher applicationEventPublisher) {
        super(
                repository,
                objectMapper,
                new JooqSqlQueryParser(repository, dslContext, sqlCustomizer),
                new JooqSqlQueryExecutor(dslContext),
                auditingService,
                applicationEventPublisher);
    }

}
