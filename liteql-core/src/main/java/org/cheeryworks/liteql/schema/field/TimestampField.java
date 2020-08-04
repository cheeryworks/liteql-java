package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

public class TimestampField extends AbstractNullableField {

    public TimestampField() {
        this(null);
    }

    public TimestampField(Boolean graphQLField) {
        super(DataType.Timestamp, graphQLField);
    }

}
