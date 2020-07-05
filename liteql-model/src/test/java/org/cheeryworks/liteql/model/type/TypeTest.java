package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TypeTest extends AbstractTest {

    @Test
    public void testingTypeParser() {
        Map<String, String> typeInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test").getPath());

        for (Map.Entry<String, String> typeInJsonFile : typeInJsonFiles.entrySet()) {
            if (typeInJsonFile.getKey().contains("definition.json")) {
                DomainType domainType = LiteQLJsonUtil.toBean(
                        getObjectMapper(), typeInJsonFile.getValue(), DomainType.class);

                getLogger().info(LiteQLJsonUtil.toJson(getObjectMapper(), domainType));
            }
        }
    }

}
