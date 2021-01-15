package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;
import org.cheeryworks.liteql.skeleton.schema.field.TimestampField;

public class DefaultTimestampField extends AbstractNullableField implements TimestampField {

    public DefaultTimestampField() {
        this(null);
    }

    public DefaultTimestampField(Boolean graphQLField) {
        super(DataType.Timestamp, graphQLField);
    }

}
