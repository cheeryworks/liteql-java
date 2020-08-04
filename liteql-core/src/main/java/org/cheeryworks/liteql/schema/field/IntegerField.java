package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

public class IntegerField extends AbstractNullableField {

    public IntegerField() {
        this(null);
    }

    public IntegerField(Boolean graphQLField) {
        super(DataType.Integer, graphQLField);
    }

}
