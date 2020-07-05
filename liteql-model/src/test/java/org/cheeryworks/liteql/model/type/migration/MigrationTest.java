package org.cheeryworks.liteql.model.type.migration;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MigrationTest extends AbstractTest {

    @Test
    public void testingMigrationParser() {
        Map<String, String> migrationInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test").getPath());

        for (Map.Entry<String, String> migrationInJsonFile : migrationInJsonFiles.entrySet()) {
            if (migrationInJsonFile.getKey().contains("/migrations/")) {
                Migration migration = LiteQLJsonUtil.toBean(
                        getObjectMapper(), migrationInJsonFile.getValue(), Migration.class);

                getLogger().info(LiteQLJsonUtil.toJson(getObjectMapper(), migration));
            }
        }
    }
}
