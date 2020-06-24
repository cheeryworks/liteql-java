package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TypeTest extends BaseTest {

    @Test
    public void testingTypeParser() {
        Map<String, String> typeInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql").getPath());

        for (Map.Entry<String, String> typeInJsonFile : typeInJsonFiles.entrySet()) {
            if (typeInJsonFile.getKey().contains("definition.json")) {
                DomainType domainType = LiteQLJsonUtil.toBean(typeInJsonFile.getValue(), DomainType.class);

                Assertions.assertEquals(
                        LiteQLJsonUtil.toJsonNode(typeInJsonFile.getValue()),
                        LiteQLJsonUtil.toJsonNode(LiteQLJsonUtil.toJson(domainType)));
            }
        }
    }

}
