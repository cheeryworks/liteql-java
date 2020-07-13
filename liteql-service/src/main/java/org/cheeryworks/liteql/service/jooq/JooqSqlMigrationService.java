package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.cheeryworks.liteql.service.migration.AbstractSqlMigrationService;
import org.jooq.DSLContext;

public class JooqSqlMigrationService extends AbstractSqlMigrationService {

    public JooqSqlMigrationService(
            Repository repository, DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(
                repository,
                new JooqSqlMigrationParser(repository, dslContext, sqlCustomizer),
                new JooqSqlMigrationExecutor(dslContext));
    }

}
