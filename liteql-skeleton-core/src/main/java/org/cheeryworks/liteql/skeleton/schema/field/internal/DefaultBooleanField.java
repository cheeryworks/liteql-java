package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;
import org.cheeryworks.liteql.skeleton.schema.field.BooleanField;

public class DefaultBooleanField extends AbstractField implements BooleanField {

    public DefaultBooleanField() {
        this(null);
    }

    public DefaultBooleanField(Boolean graphQLField) {
        super(DataType.Boolean, graphQLField);
    }

}
