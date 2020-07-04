package org.cheeryworks.liteql.service.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.query.AbstractSqlQueryService;
import org.cheeryworks.liteql.service.query.AuditingService;
import org.cheeryworks.liteql.service.repository.Repository;
import org.springframework.context.ApplicationEventPublisher;

import javax.sql.DataSource;

public class JooqSqlQueryService extends AbstractSqlQueryService {

    public JooqSqlQueryService(
            Repository repository, ObjectMapper objectMapper,
            DataSource dataSource, Database database,
            AuditingService auditingService,
            ApplicationEventPublisher applicationEventPublisher) {
        super(
                repository,
                objectMapper,
                new JooqSqlQueryParser(repository, database),
                new JooqSqlQueryExecutor(dataSource, database),
                auditingService,
                applicationEventPublisher);
    }

}
