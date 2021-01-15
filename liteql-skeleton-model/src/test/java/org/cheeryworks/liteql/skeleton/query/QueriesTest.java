package org.cheeryworks.liteql.skeleton.query;

import org.cheeryworks.liteql.skeleton.AbstractTest;
import org.cheeryworks.liteql.skeleton.util.FileReader;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
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
