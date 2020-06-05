package org.cheeryworks.liteql.sql.jooq;

import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.query.AbstractSqlQueryService;

import javax.sql.DataSource;

public class JooqSqlQueryService extends AbstractSqlQueryService {

    public JooqSqlQueryService(Repository repository, DataSource dataSource, Database database) {
        super(
                repository,
                new JooqSqlQueryParser(repository, database),
                new JooqSqlQueryExecutor(dataSource, database));
    }

}
