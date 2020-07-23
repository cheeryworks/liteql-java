package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

public class BooleanField extends AbstractField {

    public BooleanField() {
        this(null);
    }

    public BooleanField(Boolean graphQLField) {
        super(DataType.Boolean, graphQLField);
    }

    public boolean isNullable() {
        return false;
    }

}
