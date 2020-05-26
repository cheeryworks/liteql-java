package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractNullableDomainTypeField;

public class IntegerField extends AbstractNullableDomainTypeField {

    public IntegerField() {
        this.setType(DataType.Integer.name().toLowerCase());
    }

}
