package org.cheeryworks.liteql.util.builder.read;

public class LiteQLReadQueryField {

    private String name;

    private String alias;

    public LiteQLReadQueryField(String name, String alias) {
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
