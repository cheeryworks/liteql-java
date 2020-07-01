package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class ClobField extends AbstractNullableField {

    public ClobField() {
        super(DataType.Clob.name().toLowerCase());
    }

}
