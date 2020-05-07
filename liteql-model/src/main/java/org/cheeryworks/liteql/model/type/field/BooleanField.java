package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractDomainTypeField;

public class BooleanField extends AbstractDomainTypeField {

    public BooleanField() {
        this.setType(DataType.Boolean.name().toLowerCase());
    }

}
