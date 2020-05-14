package org.cheeryworks.liteql.model.type;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.util.json.JsonReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TypeTest extends BaseTest {

    @Test
    public void testingTypeParser() {
        Map<String, String> typeInJsonFiles = JsonReader.readJsonFiles(
                getClass().getResource("/liteql/types").getPath());

        for (Map.Entry<String, String> typeInJsonFile : typeInJsonFiles.entrySet()) {
            if (typeInJsonFile.getKey().contains("definition.json")) {
                DomainType domainType = LiteQLJsonUtil.toBean(typeInJsonFile.getValue(), DomainType.class);

                Assertions.assertEquals(
                        StringUtils.deleteWhitespace(typeInJsonFile.getValue()),
                        StringUtils.deleteWhitespace(LiteQLJsonUtil.toJson(domainType)));
            }
        }
    }

}
