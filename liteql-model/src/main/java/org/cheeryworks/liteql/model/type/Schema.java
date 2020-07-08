package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.type.migration.Migration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Schema implements Serializable {

    private String name;

    private Set<DomainType> domainTypes;

    private Set<TraitType> traitTypes;

    private Map<TypeName, Map<String, Migration>> migrations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<DomainType> getDomainTypes() {
        return domainTypes;
    }

    public void setDomainTypes(Set<DomainType> domainTypes) {
        this.domainTypes = domainTypes;
    }

    public Set<TraitType> getTraitTypes() {
        return traitTypes;
    }

    public void setTraitTypes(Set<TraitType> traitTypes) {
        this.traitTypes = traitTypes;
    }

    public Map<TypeName, Map<String, Migration>> getMigrations() {
        return migrations;
    }

    public void setMigrations(Map<TypeName, Map<String, Migration>> migrations) {
        this.migrations = migrations;
    }

    public Schema(String name) {
        this.name = name;
    }

}
