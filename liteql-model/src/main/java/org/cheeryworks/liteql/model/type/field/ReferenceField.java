package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.TypeName;

public class ReferenceField extends AbstractNullableField {

    private TypeName domainTypeName;

    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    public ReferenceField() {
        super(DataType.Reference);
    }

}
