package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class IntegerField extends AbstractNullableField {

    public IntegerField() {
        this.setType(DataType.Integer.name().toLowerCase());
    }

}
