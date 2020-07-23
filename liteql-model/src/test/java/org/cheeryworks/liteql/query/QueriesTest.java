package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.util.FileReader;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class QueriesTest extends AbstractTest {

    @Test
    public void testingQueriesParser() {
        Map<String, String> queriesJsonFiles = FileReader.readFiles(
                getClass().getResource("/liteql/liteql_test/queries").getPath(), "json", false);

        for (String queriesJsonFile : queriesJsonFiles.values()) {
            Queries queries = LiteQLUtil.toBean(getObjectMapper(), queriesJsonFile, Queries.class);

            getLogger().info(LiteQLUtil.toJson(getObjectMapper(), queries));
        }
    }

}
