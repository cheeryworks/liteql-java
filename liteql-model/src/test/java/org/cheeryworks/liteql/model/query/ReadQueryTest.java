package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ReadQueryTest extends BaseTest {

    @Test
    public void testingReadQueryParser() {
        Map<String, String> readQueryInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql/queries/read").getPath());

        for (String readQueryInJson : readQueryInJsonFiles.values()) {
            ReadQuery readQuery = LiteQLJsonUtil.toBean(readQueryInJson, ReadQuery.class);

            getLogger().info(LiteQLJsonUtil.toJson(readQuery));
        }
    }

}
