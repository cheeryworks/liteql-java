package org.cheeryworks.liteql.sql.jooq;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class JooqSqlSchemaMigrationExecutorFromExistDatabaseTest extends JooqSqlSchemaMigrationExecutorTest {

    public JooqSqlSchemaMigrationExecutorFromExistDatabaseTest() {
        super();
    }

    @Override
    protected String[] getInitSqls() {
        try {
            String schemaSqls = IOUtils.toString(
                    getClass().getResourceAsStream("/database/init_schema_migration_executor.sql"),
                    StandardCharsets.UTF_8);

            String[] initSqls = schemaSqls.split(";");


            String dataSqls = IOUtils.toString(
                    getClass().getResourceAsStream("/database/init_data_migration_executor.sql"),
                    StandardCharsets.UTF_8);

            initSqls = ArrayUtils.addAll(initSqls, dataSqls.split(";"));

            return initSqls;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Test
    public void testingMigrate() throws SQLException, IOException {
        super.testingMigrate();
    }

}
