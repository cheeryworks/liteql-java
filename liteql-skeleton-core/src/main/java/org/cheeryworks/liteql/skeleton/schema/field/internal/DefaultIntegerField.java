package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;
import org.cheeryworks.liteql.skeleton.schema.field.IntegerField;

public class DefaultIntegerField extends AbstractNullableField implements IntegerField {

    public DefaultIntegerField() {
        this(null);
    }

    public DefaultIntegerField(Boolean graphQLField) {
        super(DataType.Integer, graphQLField);
    }

}
