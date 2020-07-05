package org.cheeryworks.liteql.model.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.index.Index;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.util.Set;

public class DomainType extends StructType {

    private Set<Unique> uniques;

    private Set<Index> indexes;

    public Set<Unique> getUniques() {
        return uniques;
    }

    public void setUniques(Set<Unique> uniques) {
        this.uniques = uniques;
    }

    public Set<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(Set<Index> indexes) {
        this.indexes = indexes;
    }

    public DomainType() {
        super();
    }

    public DomainType(TypeName domainTypeName) {
        super(domainTypeName);
    }

    @Override
    @JsonIgnore
    public boolean isStruct() {
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

}
