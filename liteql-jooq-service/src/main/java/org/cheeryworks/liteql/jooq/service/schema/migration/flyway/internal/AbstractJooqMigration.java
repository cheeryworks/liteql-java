package org.cheeryworks.liteql.jooq.service.schema.migration.flyway.internal;

import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqMigration;

public abstract class AbstractJooqMigration implements JooqMigration {

    @Override
    public String getDescription() {
        String description = getClass().getSimpleName().substring(
                getClass().getSimpleName().indexOf("__") + 2);

        return description;
    }

}
