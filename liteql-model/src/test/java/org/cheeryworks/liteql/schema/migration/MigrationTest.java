package org.cheeryworks.liteql.schema.migration;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.util.FileReader;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MigrationTest extends AbstractTest {

    @Test
    public void testingMigrationParser() {
        Map<String, String> migrationInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test").getPath());

        for (Map.Entry<String, String> migrationInJsonFile : migrationInJsonFiles.entrySet()) {
            if (migrationInJsonFile.getKey().contains("/migrations/")) {
                Migration migration = LiteQLUtil.toBean(migrationInJsonFile.getValue(), Migration.class);

                getLogger().info(LiteQLUtil.toJson(migration));
            }
        }
    }
}
