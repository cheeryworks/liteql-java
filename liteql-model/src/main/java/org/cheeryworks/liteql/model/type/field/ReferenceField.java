package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.DomainTypeName;

public class ReferenceField extends AbstractNullableField {

    private DomainTypeName domainTypeName;

    public DomainTypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(DomainTypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    public ReferenceField() {
        super(DataType.Reference.name().toLowerCase());
    }

}
