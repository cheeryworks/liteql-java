package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.query.read.ReadQuery;
import org.cheeryworks.liteql.util.FileReader;
import org.cheeryworks.liteql.util.LiteQL;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ReadQueryTest extends AbstractTest {

    @Test
    public void testingReadQueryParser() {
        Map<String, String> readQueryInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test/queries/read").getPath());

        for (String readQueryInJson : readQueryInJsonFiles.values()) {
            ReadQuery readQuery = LiteQL.JacksonJsonUtils.toBean(readQueryInJson, ReadQuery.class);

            getLogger().info(LiteQL.JacksonJsonUtils.toJson(readQuery));
        }
    }

}
