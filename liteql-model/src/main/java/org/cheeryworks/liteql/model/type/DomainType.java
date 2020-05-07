package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.type.field.AssociationField;

import java.io.Serializable;
import java.util.List;

public class DomainType implements Serializable {

    private String schema;

    private String name;

    private Boolean embeddable;

    private List<String> embeddedTypes;

    private List<DomainTypeField> fields;

    private List<DomainTypeUniqueKey> uniques;

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

    public Boolean getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(Boolean embeddable) {
        this.embeddable = embeddable;
    }

    public List<String> getEmbeddedTypes() {
        return embeddedTypes;
    }

    public void setEmbeddedTypes(List<String> embeddedTypes) {
        this.embeddedTypes = embeddedTypes;
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

    public boolean isAssociation(String fieldName) {
        if (fields != null && fields.size() > 0) {
            for (DomainTypeField field : fields) {
                if (field instanceof AssociationField
                        && field.getName().toLowerCase().equals(fieldName.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

}
