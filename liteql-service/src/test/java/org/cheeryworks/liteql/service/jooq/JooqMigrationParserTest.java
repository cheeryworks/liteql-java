package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.service.schema.migration.jooq.JooqMigrationParser;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JooqMigrationParserTest extends AbstractJooqTest {

    private Logger logger = LoggerFactory.getLogger(JooqMigrationParserTest.class);

    private JooqMigrationParser jooqMigrationParser;

    public JooqMigrationParserTest() {
        super();

        jooqMigrationParser = new JooqMigrationParser(
                getLiteQLProperties(), getSchemaService(), getDslContext(), getSqlCustomizer());
    }

    @Test
    public void testingMigrationsToSql() {
        for (String schemaName : getSchemaService().getSchemaNames()) {
            List<String> migrationsInSql = jooqMigrationParser.migrationsToSql(schemaName);

            for (String sql : migrationsInSql) {
                logger.info(sql);
            }
        }
    }

}
