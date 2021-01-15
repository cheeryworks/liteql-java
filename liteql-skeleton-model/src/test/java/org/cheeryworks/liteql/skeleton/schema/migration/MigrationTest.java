package org.cheeryworks.liteql.skeleton.schema.migration;

import org.cheeryworks.liteql.skeleton.AbstractTest;
import org.cheeryworks.liteql.skeleton.util.FileReader;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MigrationTest extends AbstractTest {

    @Test
    public void testingMigrationParser() {
        Map<String, String> migrationInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test").getPath());

        for (Map.Entry<String, String> migrationInJsonFile : migrationInJsonFiles.entrySet()) {
            if (migrationInJsonFile.getKey().contains("/migrations/")) {
                Migration migration = LiteQL.JacksonJsonUtils.toBean(migrationInJsonFile.getValue(), Migration.class);

                getLogger().info(LiteQL.JacksonJsonUtils.toJson(migration));
            }
        }
    }
}
