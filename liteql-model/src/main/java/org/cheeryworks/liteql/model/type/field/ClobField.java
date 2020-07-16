package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class ClobField extends AbstractNullableField {

    public ClobField() {
        this(null);
    }

    public ClobField(Boolean graphQLField) {
        super(DataType.Clob, graphQLField);
    }

}
