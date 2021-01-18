package org.cheeryworks.liteql.jooq.service.schema.migration.flyway.internal;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqMigration;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqMigrationDelegate;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.internal.scanner.LocationScannerCache;
import org.flywaydb.core.internal.scanner.ResourceNameCache;
import org.flywaydb.core.internal.scanner.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractJooqMigrationDelegate implements JooqMigrationDelegate {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Class[] migrationClasses = null;

    @Override
    public Class[] getMigrationClasses() {
        if (ArrayUtils.isEmpty(migrationClasses)) {
            Scanner scanner = new Scanner(
                    JooqMigration.class,
                    Collections.singletonList(new Location("classpath:"
                            + this.getClass().getPackage().getName().replace(".", "/"))),
                    this.getClass().getClassLoader(),
                    StandardCharsets.UTF_8,
                    false,
                    new ResourceNameCache(),
                    new LocationScannerCache());

            try {
                Collection<Class<? extends JooqMigration>> classes = scanner.getClasses();

                List<Class> matchedMigrationClasses = new ArrayList<>();
                for (Class clazz : classes) {
                    if (clazz.getSimpleName().startsWith(
                            getSchemaVersionTablePrefix().toUpperCase() + "V")) {
                        matchedMigrationClasses.add(clazz);
                    }
                }

                migrationClasses = matchedMigrationClasses.toArray(new Class[matchedMigrationClasses.size()]);
            } catch (Exception ex) {
                logger.warn("Find migration classes failed", ex);
            }
        }

        return migrationClasses;
    }

}
