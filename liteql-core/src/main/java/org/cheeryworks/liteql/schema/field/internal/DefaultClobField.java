package org.cheeryworks.liteql.schema.field.internal;

import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.ClobField;

public class DefaultClobField extends AbstractNullableField implements ClobField {

    public DefaultClobField() {
        this(null);
    }

    public DefaultClobField(Boolean graphQLField) {
        super(DataType.Clob, graphQLField);
    }

}
