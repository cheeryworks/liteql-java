package org.cheeryworks.liteql.service.schema;

import java.util.Map;
import java.util.TreeMap;

public class TypeMetadata {

    private String name;

    private Map<String, String> contents = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private Map<String, String> migrationContents = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public String getName() {
        return name;
    }

    public Map<String, String> getContents() {
        return contents;
    }

    public void setContents(Map<String, String> contents) {
        this.contents = contents;
    }

    public Map<String, String> getMigrationContents() {
        return migrationContents;
    }

    public TypeMetadata(String name) {
        this.name = name;
    }

}
