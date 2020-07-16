package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class TimestampField extends AbstractNullableField {

    public TimestampField() {
        this(null);
    }

    public TimestampField(Boolean graphQLField) {
        super(DataType.Timestamp, graphQLField);
    }

}
