package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.BaseTest;
import org.cheeryworks.liteql.model.util.json.JsonReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TypeTest extends BaseTest {

    @Test
    public void testingTypeParser() {
        Map<String, String> typeInJsonFiles = JsonReader.readJsonFiles(
                getClass().getResource("/liteql/types").getPath());

        for (String typeInJson : typeInJsonFiles.values()) {
            DomainType domainType = LiteQLJsonUtil.toBean(typeInJson, DomainType.class);

            Assertions.assertEquals(
                    StringUtils.deleteWhitespace(typeInJson),
                    StringUtils.deleteWhitespace(LiteQLJsonUtil.toJson(domainType)));
        }
    }

}
