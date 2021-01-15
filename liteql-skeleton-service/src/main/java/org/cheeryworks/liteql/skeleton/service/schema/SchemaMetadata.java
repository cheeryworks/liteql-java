package org.cheeryworks.liteql.skeleton.service.schema;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SchemaMetadata {

    public static final String VERSION_CONCAT = "__";

    public static final String VERSION_BASELINE_SUFFIX = ".0";

    private String name;

    private Map<String, TypeMetadata> typeMetadataSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public String getName() {
        return name;
    }

    public Map<String, TypeMetadata> getTypeMetadataSet() {
        return typeMetadataSet;
    }

    public SchemaMetadata(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SchemaMetadata)) {
            return false;
        }

        SchemaMetadata that = (SchemaMetadata) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
