package org.cheeryworks.liteql.service.schema;

public class MigrationDefinition {

    private String name;

    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MigrationDefinition(String name, String content) {
        this.name = name;
        this.content = content;
    }

}
