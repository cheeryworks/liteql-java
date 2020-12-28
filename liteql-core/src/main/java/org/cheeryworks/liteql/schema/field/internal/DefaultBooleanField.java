package org.cheeryworks.liteql.schema.field.internal;

import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.BooleanField;

public class DefaultBooleanField extends AbstractField implements BooleanField {

    public DefaultBooleanField() {
        this(null);
    }

    public DefaultBooleanField(Boolean graphQLField) {
        super(DataType.Boolean, graphQLField);
    }

}
