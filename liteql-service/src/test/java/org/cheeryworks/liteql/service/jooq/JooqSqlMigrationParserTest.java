package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.AbstractSqlTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JooqSqlMigrationParserTest extends AbstractSqlTest {

    private Logger logger = LoggerFactory.getLogger(JooqSqlMigrationParserTest.class);

    private JooqSqlMigrationParser jooqSqlSchemaMigrationParser;

    public JooqSqlMigrationParserTest() {
        super();

        jooqSqlSchemaMigrationParser = new JooqSqlMigrationParser(getRepository(), getDslContext(), null);
    }

    @Test
    public void testingMigrationsToSql() {
        for (String schemaName : getRepository().getSchemaNames()) {
            List<String> migrationsInSql = jooqSqlSchemaMigrationParser.migrationsToSql(schemaName);

            for (String sql : migrationsInSql) {
                logger.info(sql);
            }
        }
    }

}
