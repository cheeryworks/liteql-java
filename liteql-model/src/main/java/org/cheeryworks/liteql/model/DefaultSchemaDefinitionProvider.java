package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.SchemaDefinitionProvider;
import org.cheeryworks.liteql.util.LiteQL;

public class DefaultSchemaDefinitionProvider implements SchemaDefinitionProvider {

    @Override
    public String getSchema() {
        return LiteQL.Constants.SCHEMA;
    }

    @Override
    public String getVersion() {
        return LiteQL.Constants.SPECIFICATION_VERSION;
    }

}
