package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.SchemaDefinitionProvider;
import org.cheeryworks.liteql.util.LiteQLConstants;

public class DefaultSchemaDefinitionProvider implements SchemaDefinitionProvider {

    @Override
    public String getSchema() {
        return LiteQLConstants.SCHEMA;
    }

    @Override
    public String getVersion() {
        return LiteQLConstants.SPECIFICATION_VERSION;
    }

}
