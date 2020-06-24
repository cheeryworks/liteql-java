package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class QueriesTest extends BaseTest {

    @Test
    public void testingQueriesParser() {
        Map<String, String> queriesJsonFiles = FileReader.readFiles(
                getClass().getResource("/liteql/liteql/queries").getPath(), "json", false);

        for (String queriesJsonFile : queriesJsonFiles.values()) {
            Queries queries = LiteQLJsonUtil.toBean(queriesJsonFile, Queries.class);

            getLogger().info(LiteQLJsonUtil.toJson(queries));
        }
    }

}
