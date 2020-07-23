package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.util.FileReader;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class DomainTypeTest extends AbstractTest {

    @Test
    public void testingTypeParser() {
        Map<String, String> typeInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test").getPath());

        for (Map.Entry<String, String> typeInJsonFile : typeInJsonFiles.entrySet()) {
            if (typeInJsonFile.getKey().contains("definition.json")) {
                DomainType domainType = LiteQLUtil.toBean(
                        getObjectMapper(), typeInJsonFile.getValue(), DomainType.class);

                getLogger().info(LiteQLUtil.toJson(getObjectMapper(), domainType));
            }
        }
    }

}
