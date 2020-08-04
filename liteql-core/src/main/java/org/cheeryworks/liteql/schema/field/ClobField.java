package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

public class ClobField extends AbstractNullableField {

    public ClobField() {
        this(null);
    }

    public ClobField(Boolean graphQLField) {
        super(DataType.Clob, graphQLField);
    }

}
