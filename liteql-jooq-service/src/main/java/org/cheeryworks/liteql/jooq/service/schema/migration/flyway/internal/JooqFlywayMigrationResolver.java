package org.cheeryworks.liteql.jooq.service.schema.migration.flyway.internal;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrationTransactionController;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigration;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrationDelegate;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.resolver.Context;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.internal.resolver.ResolvedMigrationImpl;
import org.flywaydb.core.internal.util.ClassUtils;
import org.flywaydb.core.internal.util.StringUtils;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JooqFlywayMigrationResolver implements MigrationResolver {

    private JooqFlywayMigrationDelegate migrationDelegate;

    private DSLContext dslContext;

    private JooqFlywayMigrationTransactionController transactionController;

    public JooqFlywayMigrationResolver(
            JooqFlywayMigrationDelegate migrationDelegate, DSLContext dslContext,
            JooqFlywayMigrationTransactionController transactionController) {
        this.migrationDelegate = migrationDelegate;
        this.dslContext = dslContext;
        this.transactionController = transactionController;
    }

    @Override
    public Collection<ResolvedMigration> resolveMigrations(Context context) {
        List<ResolvedMigration> migrations = new ArrayList<>();

        Class[] migrationClasses = migrationDelegate.getMigrationClasses();

        if (!ArrayUtils.isEmpty(migrationClasses)) {
            for (Class migrationClass : migrationClasses) {
                try {
                    JooqFlywayMigration migration
                            = (JooqFlywayMigration) migrationClass.getDeclaredConstructor().newInstance();

                    MigrationVersion version = getVersion(migration, context.getConfiguration());
                    String description = migration.getDescription();
                    if (!StringUtils.hasText(description)) {
                        throw new FlywayException("Missing description for migration " + version);
                    }

                    ResolvedMigrationImpl resolvedMigration = new ResolvedMigrationImpl(
                            version, description, migration.getClass().getSimpleName(),
                            null, null, MigrationType.CUSTOM, ClassUtils.getLocationOnDisk(migrationClass),
                            new JooqFlywayMigrationExecutor(migration, dslContext, transactionController)
                    );

                    migrations.add(resolvedMigration);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }

        return migrations;
    }

    private MigrationVersion getVersion(JooqFlywayMigration migration, Configuration configuration) {
        String latestVersion = migration.getClass().getSimpleName().substring(
                configuration.getSqlMigrationPrefix().length(),
                migration.getClass().getSimpleName().indexOf("__"));

        return MigrationVersion.fromVersion(latestVersion);
    }

}
