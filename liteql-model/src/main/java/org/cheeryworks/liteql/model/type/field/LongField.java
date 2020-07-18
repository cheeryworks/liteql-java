package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class LongField extends AbstractNullableField {

    public LongField() {
        this(null);
    }

    public LongField(Boolean graphQLField) {
        super(DataType.Long, graphQLField);
    }

}
