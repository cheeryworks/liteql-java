package org.cheeryworks.liteql.skeleton.schema.field.internal;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.enums.DataType;
import org.cheeryworks.liteql.skeleton.schema.field.ReferenceField;

public class DefaultReferenceField extends AbstractNullableField implements ReferenceField {

    private TypeName domainTypeName;

    private TypeName mappedDomainTypeName;

    private Boolean collection;

    @Override
    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    @Override
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

    @Override
    public boolean isCollection() {
        if (collection == null) {
            return false;
        }

        return collection.booleanValue();
    }

    public void setCollection(Boolean collection) {
        this.collection = collection;
    }

    public DefaultReferenceField() {
        this(null);
    }

    public DefaultReferenceField(Boolean graphQLField) {
        super(DataType.Reference, graphQLField);
    }

}
