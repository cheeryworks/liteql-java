package org.cheeryworks.liteql.model.type.migration;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MigrationTest extends AbstractTest {

    @Test
    @Disabled
    public void testingMigrationParser() {
        Map<String, String> migrationInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql").getPath());

        for (Map.Entry<String, String> migrationInJsonFile : migrationInJsonFiles.entrySet()) {
            if (migrationInJsonFile.getKey().contains("/migrations/")) {
                Migration migration = LiteQLJsonUtil.toBean(
                        getObjectMapper(), migrationInJsonFile.getValue(), Migration.class);

                Assertions.assertEquals(
                        LiteQLJsonUtil.toJsonNode(getObjectMapper(), migrationInJsonFile.getValue()),
                        LiteQLJsonUtil.toJsonNode(
                                getObjectMapper(), LiteQLJsonUtil.toJson(getObjectMapper(), migration)));
            }
        }
    }
}
