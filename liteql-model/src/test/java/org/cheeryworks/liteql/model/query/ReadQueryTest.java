package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ReadQueryTest extends AbstractTest {

    @Test
    public void testingReadQueryParser() {
        Map<String, String> readQueryInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql/queries/read").getPath());

        for (String readQueryInJson : readQueryInJsonFiles.values()) {
            ReadQuery readQuery = LiteQLJsonUtil.toBean(getObjectMapper(), readQueryInJson, ReadQuery.class);

            getLogger().info(LiteQLJsonUtil.toJson(getObjectMapper(), readQuery));
        }
    }

}
