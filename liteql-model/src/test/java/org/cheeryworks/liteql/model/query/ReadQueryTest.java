package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.util.json.JsonReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ReadQueryTest extends BaseTest {

    @Test
    public void testingReadQueryParser() {
        Map<String, String> readQueryInJsonFiles = JsonReader.readJsonFiles(
                getClass().getResource("/liteql/queries/read").getPath());

        for (String readQueryInJson : readQueryInJsonFiles.values()) {
            ReadQuery readQuery = LiteQLJsonUtil.toBean(readQueryInJson, ReadQuery.class);

            getLogger().info(LiteQLJsonUtil.toJson(readQuery));
        }
    }

}