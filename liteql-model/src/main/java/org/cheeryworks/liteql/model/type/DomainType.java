package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.type.field.ReferenceField;

import java.io.Serializable;
import java.util.List;

public class DomainType implements Serializable {

    private String schema;

    private String name;

    private List<DomainTypeField> fields;

    private List<DomainTypeUniqueKey> uniques;

    private List<DomainTypeIndexKey> indexes;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DomainTypeField> getFields() {
        return fields;
    }

    public void setFields(List<DomainTypeField> fields) {
        this.fields = fields;
    }

    public List<DomainTypeUniqueKey> getUniques() {
        return uniques;
    }

    public void setUniques(List<DomainTypeUniqueKey> uniques) {
        this.uniques = uniques;
    }

    public List<DomainTypeIndexKey> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<DomainTypeIndexKey> indexes) {
        this.indexes = indexes;
    }

    public boolean isReferenceField(String fieldName) {
        if (fields != null && fields.size() > 0) {
            for (DomainTypeField field : fields) {
                if (field instanceof ReferenceField
                        && field.getName().toLowerCase().equals(fieldName.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

}
