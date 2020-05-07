package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.util.json.JsonReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class QueriesTest extends BaseTest {

    @Test
    public void testingQueriesParser() {
        Map<String, String> queriesJsonFiles = JsonReader.readJsonFiles(
                getClass().getResource("/liteql/queries").getPath(), false);

        for (String queriesJsonFile : queriesJsonFiles.values()) {
            Queries queries = LiteQLJsonUtil.toBean(queriesJsonFile, Queries.class);

            getLogger().info(LiteQLJsonUtil.toJson(queries));
        }
    }

}
