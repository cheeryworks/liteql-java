package org.cheeryworks.liteql.schema;

public interface SchemaDefinitionProvider {

    default String[] getPackages() {
        return new String[]{getClass().getPackage().getName()};
    }

    String getSchema();

    String getVersion();

}
