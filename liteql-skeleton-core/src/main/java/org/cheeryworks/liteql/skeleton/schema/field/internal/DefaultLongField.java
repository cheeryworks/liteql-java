package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;
import org.cheeryworks.liteql.skeleton.schema.field.LongField;

public class DefaultLongField extends AbstractNullableField implements LongField {

    public DefaultLongField() {
        this(null);
    }

    public DefaultLongField(Boolean graphQLField) {
        super(DataType.Long, graphQLField);
    }

}
