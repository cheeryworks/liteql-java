package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.Type;

public class ReferenceField extends AbstractNullableField {

    private Type domainType;

    public Type getDomainType() {
        return domainType;
    }

    public void setDomainType(Type domainType) {
        this.domainType = domainType;
    }

    public ReferenceField() {
        super(DataType.Reference);
    }

}
