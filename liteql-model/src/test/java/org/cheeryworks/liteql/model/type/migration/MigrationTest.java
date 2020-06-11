package org.cheeryworks.liteql.model.type.migration;

import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MigrationTest extends BaseTest {

    @Test
    public void testingMigrationParser() {
        Map<String, String> migrationInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/types/").getPath());

        for (Map.Entry<String, String> migrationInJsonFile : migrationInJsonFiles.entrySet()) {
            if (migrationInJsonFile.getKey().contains("/migrations/")) {
                Migration migration = LiteQLJsonUtil.toBean(migrationInJsonFile.getValue(), Migration.class);

                Assertions.assertEquals(
                        LiteQLJsonUtil.toJsonNode(migrationInJsonFile.getValue()),
                        LiteQLJsonUtil.toJsonNode(LiteQLJsonUtil.toJson(migration)));
            }
        }
    }
}
