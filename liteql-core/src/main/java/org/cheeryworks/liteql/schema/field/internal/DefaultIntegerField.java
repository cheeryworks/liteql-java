package org.cheeryworks.liteql.schema.field.internal;

import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.IntegerField;

public class DefaultIntegerField extends AbstractNullableField implements IntegerField {

    public DefaultIntegerField() {
        this(null);
    }

    public DefaultIntegerField(Boolean graphQLField) {
        super(DataType.Integer, graphQLField);
    }

}
