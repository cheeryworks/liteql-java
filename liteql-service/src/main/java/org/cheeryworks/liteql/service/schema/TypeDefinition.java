package org.cheeryworks.liteql.service.schema;

import java.util.Map;
import java.util.TreeMap;

public class TypeDefinition {

    private String name;

    private String content;

    private Map<String, MigrationDefinition> migrationDefinitions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, MigrationDefinition> getMigrationDefinitions() {
        return migrationDefinitions;
    }

    public TypeDefinition(String name) {
        this.name = name;
    }

}
