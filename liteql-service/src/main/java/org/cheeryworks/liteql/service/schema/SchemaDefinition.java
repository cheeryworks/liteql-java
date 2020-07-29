package org.cheeryworks.liteql.service.schema;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SchemaDefinition {

    public static final String VERSION_CONCAT = "__";

    public static final String VERSION_BASELINE_SUFFIX = ".0";

    private String name;

    private Map<String, TypeDefinition> typeDefinitions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public String getName() {
        return name;
    }

    public Map<String, TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    public SchemaDefinition(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SchemaDefinition)) {
            return false;
        }

        SchemaDefinition that = (SchemaDefinition) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
