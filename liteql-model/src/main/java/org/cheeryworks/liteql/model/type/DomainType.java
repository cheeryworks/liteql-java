package org.cheeryworks.liteql.model.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.index.Index;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.util.Set;

public class DomainType extends TraitType {

    public static final String DOMAIN_TYPE_NAME_KEY = "domainTypeName";

    private Set<Unique> uniques;

    private Set<Index> indexes;

    public Set<Unique> getUniques() {
        return this.uniques;
    }

    public Set<Index> getIndexes() {
        return this.indexes;
    }

    public void setUniques(Set<Unique> uniques) {
        this.uniques = uniques;
    }

    public void setIndexes(Set<Index> indexes) {
        this.indexes = indexes;
    }

    public DomainType() {
    }

    public DomainType(TypeName typeName) {
        super(typeName);
    }

    public DomainType(String schema, String name) {
        super(schema, name);
    }

    @Override
    @JsonIgnore
    public boolean isTrait() {
        return false;
    }

    public boolean isReferenceField(String fieldName) {
        if (getFields() != null && getFields().size() > 0) {
            for (Field field : getFields()) {
                if (field instanceof ReferenceField
                        && field.getName().toLowerCase().equals(fieldName.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    public ReferenceField getReferenceField(String referenceFieldName) {
        for (Field field : getFields()) {
            if (field instanceof ReferenceField && field.getName().equalsIgnoreCase(referenceFieldName)) {
                return (ReferenceField) field;
            }
        }

        return null;
    }

}
