package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.AbstractSqlTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JooqSqlSchemaMigrationParserTest extends AbstractSqlTest {

    private Logger logger = LoggerFactory.getLogger(JooqSqlSchemaMigrationParserTest.class);

    private JooqSqlSchemaMigrationParser jooqSqlSchemaMigrationParser;

    public JooqSqlSchemaMigrationParserTest() {
        super();

        jooqSqlSchemaMigrationParser = new JooqSqlSchemaMigrationParser(getRepository(), getDatabase());
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
