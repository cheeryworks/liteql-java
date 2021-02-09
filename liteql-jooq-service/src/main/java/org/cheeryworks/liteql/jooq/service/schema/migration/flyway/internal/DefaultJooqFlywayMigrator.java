package org.cheeryworks.liteql.jooq.service.schema.migration.flyway.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrator;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrationTransactionController;
import org.cheeryworks.liteql.jooq.util.JooqUtil;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrationDelegate;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationType;
import org.jooq.DSLContext;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultJooqFlywayMigrator implements JooqFlywayMigrator {

    private DataSource dataSource;

    private DSLContext dslContext;

    private JooqFlywayMigrationTransactionController jooqFlywayMigrationTransactionController;

    public DefaultJooqFlywayMigrator(
            DataSource dataSource, DSLContext dslContext,
            JooqFlywayMigrationTransactionController jooqFlywayMigrationTransactionController) {
        this.dataSource = dataSource;
        this.dslContext = dslContext;
        this.jooqFlywayMigrationTransactionController = jooqFlywayMigrationTransactionController;
    }

    @Override
    public void migrate() {
        Set<String> finishedMigrations = new HashSet<>();

        Set<String> migratedPackages = new HashSet<>();

        List<JooqFlywayMigrationDelegate> migrationDelegates = ServiceLoader.load(JooqFlywayMigrationDelegate.class)
                .stream()
                .map(jooqMigrationDelegateProvider -> jooqMigrationDelegateProvider.get())
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(migrationDelegates)) {
            while (finishedMigrations.size() != migrationDelegates.size()) {
                for (JooqFlywayMigrationDelegate migrationDelegate : migrationDelegates) {
                    String migrationDelegatePackageName = migrationDelegate.getClass().getPackage().getName();

                    int migratingPackageNameSuffix = migrationDelegatePackageName.indexOf(".migration");

                    if (migrationDelegatePackageName.contains(".repository.jdbc.migration")) {
                        migratingPackageNameSuffix = migrationDelegatePackageName.indexOf(
                                ".repository.jdbc.migration");
                    }

                    String migratingPackageName = migrationDelegatePackageName
                            .substring(0, migratingPackageNameSuffix)
                            + ".model";

                    if (!isMigrated(finishedMigrations, migrationDelegate)
                            && isReady(migratedPackages, migrationDelegate)) {
                        Flyway flyway = Flyway.configure()
                                .dataSource(dataSource)
                                .sqlMigrationPrefix(
                                        migrationDelegate.getSchemaVersionTablePrefix().toUpperCase() + "V")
                                .repeatableSqlMigrationPrefix(
                                        migrationDelegate.getSchemaVersionTablePrefix().toUpperCase() + "CR")
                                .table(migrationDelegate.getSchemaVersionTablePrefix()
                                        + JooqFlywayMigrationDelegate.SCHEMA_VERSION_TABLE_SUFFIX)
                                .resolvers(
                                        new JooqFlywayMigrationResolver(
                                                migrationDelegate, dslContext, jooqFlywayMigrationTransactionController))
                                .locations("db/" + JooqUtil.getDatabase(dslContext.dialect()).name().toLowerCase())
                                .load();

                        migrate(flyway, migrationDelegate);

                        migratedPackages.add(migratingPackageName);
                        finishedMigrations.add(migrationDelegate.getClass().getName());
                    }
                }
            }
        }
    }

    @Override
    public void clean() {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        flyway.clean();
    }

    private void migrate(Flyway flyway, JooqFlywayMigrationDelegate migrationDelegate) {
        if (needToBaseline(flyway)) {
            flyway.configure().baselineVersion(migrationDelegate.getBaselineVersion());
            flyway.configure().baselineDescription(migrationDelegate.getBaselineVersion() + " Baseline");
            flyway.baseline();
        }

        flyway.repair();
        flyway.migrate();
    }

    private boolean needToBaseline(Flyway flyway) {
        boolean needToBaseline = true;

        MigrationInfo[] appliedMigrations = flyway.info().applied();

        for (MigrationInfo appliedMigration : appliedMigrations) {
            if (appliedMigration.getType().equals(MigrationType.BASELINE)) {
                needToBaseline = false;
                break;
            }
        }

        return needToBaseline;
    }

    private boolean isMigrated(Set<String> finishedMigrations, JooqFlywayMigrationDelegate migrationDelegate) {
        if (finishedMigrations.contains(migrationDelegate.getClass().getName())) {
            return true;
        }

        return false;
    }

    private boolean isReady(Set<String> migratedPackages, JooqFlywayMigrationDelegate migrationDelegate) {
        if (migrationDelegate.getPreMigratedPackages() != null) {
            for (Package preMigratedPackage
                    : migrationDelegate.getPreMigratedPackages()) {
                if (!migratedPackages.contains(preMigratedPackage.getName())) {
                    return false;
                }
            }
        }

        return true;
    }

}
