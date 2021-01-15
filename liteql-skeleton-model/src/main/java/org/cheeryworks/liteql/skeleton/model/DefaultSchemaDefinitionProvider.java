package org.cheeryworks.liteql.skeleton.model;

import org.cheeryworks.liteql.skeleton.schema.SchemaDefinitionProvider;
import org.cheeryworks.liteql.skeleton.util.LiteQL;

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
