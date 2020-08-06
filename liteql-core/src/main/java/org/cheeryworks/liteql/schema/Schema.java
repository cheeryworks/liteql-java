package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.schema.migration.Migration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Schema implements Serializable {

    public static final String SUFFIX_OF_SCHEMA_ROOT_FILE = ".yml";

    public static final String NAME_OF_TYPES_DIRECTORY = "types";

    public static final String NAME_OF_MIGRATIONS_DIRECTORY = "migrations";

    public static final String SUFFIX_OF_CONFIGURATION_FILE = ".json";

    public static final String SUFFIX_OF_TYPE_DEFINITION = "definition" + SUFFIX_OF_CONFIGURATION_FILE;

    public static final String VERSION_CONCAT = "__";

    private String name;

    private Set<DomainTypeDefinition> domainTypeDefinitions = new TreeSet<>(
            (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(
                    o1.getTypeName().getName(), o2.getTypeName().getName()));

    private Set<TraitTypeDefinition> traitTypeDefinitions = new TreeSet<>(
            (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(
                    o1.getTypeName().getName(), o2.getTypeName().getName()));

    private Map<TypeName, Map<String, Migration>> migrations = new TreeMap<>(
            (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(
                    o1.getName(), o2.getName()));

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<DomainTypeDefinition> getDomainTypeDefinitions() {
        return domainTypeDefinitions;
    }

    public Set<TraitTypeDefinition> getTraitTypeDefinitions() {
        return traitTypeDefinitions;
    }

    public Map<TypeName, Map<String, Migration>> getMigrations() {
        return migrations;
    }

    public Schema(String name) {
        this.name = name;
    }

}
