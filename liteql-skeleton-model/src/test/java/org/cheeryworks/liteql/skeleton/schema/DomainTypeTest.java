package org.cheeryworks.liteql.skeleton.schema;

import org.cheeryworks.liteql.skeleton.AbstractTest;
import org.cheeryworks.liteql.skeleton.util.FileReader;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.cheeryworks.liteql.skeleton.schema.Schema.SUFFIX_OF_TYPE_DEFINITION;

public class DomainTypeTest extends AbstractTest {

    @Test
    public void testingTypeParser() {
        Map<String, String> typeInJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test").getPath());

        for (Map.Entry<String, String> typeInJsonFile : typeInJsonFiles.entrySet()) {
            if (typeInJsonFile.getKey().contains(SUFFIX_OF_TYPE_DEFINITION)) {
                DomainTypeDefinition domainTypeDefinition = LiteQL.JacksonJsonUtils.toBean(
                        typeInJsonFile.getValue(), DomainTypeDefinition.class);

                getLogger().info(LiteQL.JacksonJsonUtils.toJson(domainTypeDefinition));
            }
        }
    }

}
