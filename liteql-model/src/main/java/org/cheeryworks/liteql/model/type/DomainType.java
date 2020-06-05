package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.index.Index;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.io.Serializable;
import java.util.List;

public class DomainType implements Serializable {

    private String schema;

    private String name;

    private List<Field> fields;

    private List<Unique> uniques;

    private List<Index> indexes;

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
