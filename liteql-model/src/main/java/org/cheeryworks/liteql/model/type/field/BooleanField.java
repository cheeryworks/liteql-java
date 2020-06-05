package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class BooleanField extends AbstractField {

    public BooleanField() {
        this.setType(DataType.Boolean.name().toLowerCase());
    }

}
