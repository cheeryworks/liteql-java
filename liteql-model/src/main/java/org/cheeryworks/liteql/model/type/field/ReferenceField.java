package org.cheeryworks.liteql.model.type.field;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.TypeName;

public class ReferenceField extends AbstractNullableField {

    private TypeName domainTypeName;

    private TypeName mappedDomainTypeName;

    private Boolean collection;

    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    public TypeName getMappedDomainTypeName() {
        return mappedDomainTypeName;
    }

    public void setMappedDomainTypeName(TypeName mappedDomainTypeName) {
        this.mappedDomainTypeName = mappedDomainTypeName;
    }

    @JsonGetter
    private Boolean collection() {
        return collection;
    }

    public boolean isCollection() {
        if (collection == null) {
            return false;
        }

        return collection.booleanValue();
    }

    public void setCollection(Boolean collection) {
        this.collection = collection;
    }

    public ReferenceField() {
        this(null);
    }

    public ReferenceField(Boolean graphQLField) {
        super(DataType.Reference, graphQLField);
    }

}
