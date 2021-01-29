package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.field.LongField;

public class DefaultLongField extends AbstractNullableField implements LongField {

    public DefaultLongField() {
        super();
    }

    public DefaultLongField(Boolean graphQLField) {
        super(graphQLField);
    }

}
