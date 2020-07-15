package org.cheeryworks.liteql.model.util.builder.query.read;

public class ReadQueryFieldMetadata {

    private String name;

    private String alias;

    public ReadQueryFieldMetadata(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

}
