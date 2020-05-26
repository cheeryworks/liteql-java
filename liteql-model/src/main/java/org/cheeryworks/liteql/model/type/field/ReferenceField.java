package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractNullableDomainTypeField;

public class ReferenceField extends AbstractNullableDomainTypeField {

    private String domainType;

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public ReferenceField() {
        this.setType(DataType.Reference.name().toLowerCase());
    }

}
