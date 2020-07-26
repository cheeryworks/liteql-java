package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.schema.migration.Migration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Schema implements Serializable {

    private String name;

    private Set<DomainType> domainTypes = new TreeSet<>(
            (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(
                    o1.getTypeName().getName(), o2.getTypeName().getName()));

    private Set<TraitType> traitTypes = new TreeSet<>(
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

    public Set<DomainType> getDomainTypes() {
        return domainTypes;
    }

    public Set<TraitType> getTraitTypes() {
        return traitTypes;
    }

    public Map<TypeName, Map<String, Migration>> getMigrations() {
        return migrations;
    }

    public Schema(String name) {
        this.name = name;
    }

}
