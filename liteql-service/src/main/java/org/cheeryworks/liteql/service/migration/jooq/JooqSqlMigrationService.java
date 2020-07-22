package org.cheeryworks.liteql.service.migration.jooq;

import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.service.migration.AbstractSqlMigrationService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.DSLContext;

public class JooqSqlMigrationService extends AbstractSqlMigrationService {

    public JooqSqlMigrationService(
            Repository repository, DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(
                repository,
                new JooqSqlMigrationParser(repository, dslContext, sqlCustomizer),
                new JooqSqlMigrationExecutor(dslContext, sqlCustomizer));
    }

}
