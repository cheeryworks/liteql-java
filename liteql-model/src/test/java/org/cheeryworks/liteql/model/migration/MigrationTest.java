package org.cheeryworks.liteql.model.migration;

import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.util.json.JsonReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MigrationTest extends BaseTest {

    @Test
    public void testingMigrationParser() {
        Map<String, String> migrationInJsonFiles = JsonReader.readJsonFiles(
                getClass().getResource("/liteql/migrations").getPath());

        for (String migrationInJson : migrationInJsonFiles.values()) {
            Migration migration = LiteQLJsonUtil.toBean(migrationInJson, Migration.class);

            Assertions.assertEquals(
                    StringUtils.deleteWhitespace(migrationInJson),
                    StringUtils.deleteWhitespace(LiteQLJsonUtil.toJson(migration)));
        }
    }

}
