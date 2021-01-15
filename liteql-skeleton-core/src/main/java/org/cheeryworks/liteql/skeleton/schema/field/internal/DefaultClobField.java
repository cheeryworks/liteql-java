package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;
import org.cheeryworks.liteql.skeleton.schema.field.ClobField;

public class DefaultClobField extends AbstractNullableField implements ClobField {

    public DefaultClobField() {
        this(null);
    }

    public DefaultClobField(Boolean graphQLField) {
        super(DataType.Clob, graphQLField);
    }

}
