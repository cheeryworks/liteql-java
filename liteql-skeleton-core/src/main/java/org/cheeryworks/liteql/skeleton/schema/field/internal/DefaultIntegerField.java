package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.field.IntegerField;

public class DefaultIntegerField extends AbstractNullableField implements IntegerField {

    public DefaultIntegerField() {
        super();
    }

    public DefaultIntegerField(Boolean graphQLField) {
        super(graphQLField);
    }

}
