package org.cheeryworks.liteql.jooq.service.schema.migration;

import org.cheeryworks.liteql.jooq.service.query.JooqQueryParser;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.service.schema.migration.AbstractSqlMigrationService;
import org.cheeryworks.liteql.skeleton.service.schema.migration.MigrationEventPublisher;

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
