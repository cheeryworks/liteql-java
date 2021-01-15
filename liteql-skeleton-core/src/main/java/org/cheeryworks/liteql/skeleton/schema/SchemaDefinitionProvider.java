package org.cheeryworks.liteql.skeleton.schema;

public interface SchemaDefinitionProvider {

    default String[] getPackages() {
        return new String[]{getClass().getPackage().getName()};
    }

    String getSchema();

    String getVersion();

}
