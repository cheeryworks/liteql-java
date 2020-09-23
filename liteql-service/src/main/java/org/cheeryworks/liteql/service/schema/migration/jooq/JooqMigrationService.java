package org.cheeryworks.liteql.service.schema.migration.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.query.jooq.JooqQueryParser;
import org.cheeryworks.liteql.service.schema.migration.AbstractSqlMigrationService;
import org.cheeryworks.liteql.service.schema.migration.MigrationEventPublisher;

public class JooqMigrationService extends AbstractSqlMigrationService {

    public JooqMigrationService(
            LiteQLProperties liteQLProperties, JooqQueryParser jooqQueryParser,
            MigrationEventPublisher migrationEventPublisher) {
        super(
                new JooqMigrationParser(liteQLProperties, jooqQueryParser),
                new JooqMigrationExecutor(liteQLProperties, jooqQueryParser.getDslContext()),
                migrationEventPublisher);
    }

}
