package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.field.TimestampField;

public class DefaultTimestampField extends AbstractNullableField implements TimestampField {

    public DefaultTimestampField() {
        super();
    }

    public DefaultTimestampField(Boolean graphQLField) {
        super(graphQLField);
    }

}
