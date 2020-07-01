package org.cheeryworks.liteql.model.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.index.Index;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.util.List;

public class DomainType extends DomainTypeName {

    private List<Field> fields;

    private List<Unique> uniques;

    private List<Index> indexes;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Unique> getUniques() {
        return uniques;
    }

    public void setUniques(List<Unique> uniques) {
        this.uniques = uniques;
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    public DomainType() {
        super();
    }

    public DomainType(DomainTypeName domainTypeName) {
        super(domainTypeName.getSchema(), domainTypeName.getName());
    }

    @JsonIgnore
    public boolean isReferenceField(String fieldName) {
        if (fields != null && fields.size() > 0) {
            for (Field field : fields) {
                if (field instanceof ReferenceField
                        && field.getName().toLowerCase().equals(fieldName.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

}
