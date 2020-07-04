package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.index.Index;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.util.Set;

public class DomainType extends DomainTypeName {

    private Set<Field> fields;

    private Set<Unique> uniques;

    private Set<Index> indexes;

    private Set<DomainInterfaceName> interfaces;

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

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

    public Set<DomainInterfaceName> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<DomainInterfaceName> interfaces) {
        this.interfaces = interfaces;
    }

    public DomainType() {
        super();
    }

    public DomainType(DomainTypeName domainTypeName) {
        super(domainTypeName.getSchema(), domainTypeName.getName());
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

    public boolean implement(String name) {
        if (interfaces != null && interfaces.size() > 0) {
            for (DomainInterfaceName domainInterfaceName : interfaces) {
                if (domainInterfaceName.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

}
