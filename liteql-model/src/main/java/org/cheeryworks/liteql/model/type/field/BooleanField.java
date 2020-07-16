package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

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
