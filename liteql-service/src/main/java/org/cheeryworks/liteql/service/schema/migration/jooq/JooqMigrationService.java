package org.cheeryworks.liteql.service.schema.migration.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.schema.migration.AbstractSqlMigrationService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.DSLContext;

public class JooqMigrationService extends AbstractSqlMigrationService {

    public JooqMigrationService(
            LiteQLProperties liteQLProperties, SchemaService schemaService,
            DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(
                schemaService,
                new JooqMigrationParser(liteQLProperties, schemaService, dslContext, sqlCustomizer),
                new JooqMigrationExecutor(liteQLProperties, dslContext));
    }

}
