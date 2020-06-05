package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class ClobField extends AbstractNullableField {

    public ClobField() {
        this.setType(DataType.Clob.name().toLowerCase());
    }

}
