package org.cheeryworks.liteql.service.jooq;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JooqSqlMigrationParserTest extends AbstractJooqSqlTest {

    private Logger logger = LoggerFactory.getLogger(JooqSqlMigrationParserTest.class);

    private JooqSqlMigrationParser jooqSqlMigrationParser;

    public JooqSqlMigrationParserTest() {
        super();

        jooqSqlMigrationParser = new JooqSqlMigrationParser(getRepository(), getDslContext(), null);
    }

    @Test
    public void testingMigrationsToSql() {
        for (String schemaName : getRepository().getSchemaNames()) {
            List<String> migrationsInSql = jooqSqlMigrationParser.migrationsToSql(schemaName);

            for (String sql : migrationsInSql) {
                logger.info(sql);
            }
        }
    }

}
