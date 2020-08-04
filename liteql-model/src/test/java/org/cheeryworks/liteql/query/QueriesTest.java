package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.util.FileReader;
import org.cheeryworks.liteql.util.LiteQL;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class QueriesTest extends AbstractTest {

    @Test
    public void testingQueriesParser() {
        Map<String, String> queriesJsonFiles = FileReader.readFiles(
                getClass().getResource("/liteql/liteql_test/queries").getPath(), "json", false);

        for (String queriesJsonFile : queriesJsonFiles.values()) {
            Queries queries = LiteQL.JacksonJsonUtils.toBean(queriesJsonFile, Queries.class);

            getLogger().info(LiteQL.JacksonJsonUtils.toJson(queries));
        }
    }

}
