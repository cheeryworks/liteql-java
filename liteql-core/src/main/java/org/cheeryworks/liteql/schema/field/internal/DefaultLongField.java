package org.cheeryworks.liteql.schema.field.internal;

import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.LongField;

public class DefaultLongField extends AbstractNullableField implements LongField {

    public DefaultLongField() {
        this(null);
    }

    public DefaultLongField(Boolean graphQLField) {
        super(DataType.Long, graphQLField);
    }

}
