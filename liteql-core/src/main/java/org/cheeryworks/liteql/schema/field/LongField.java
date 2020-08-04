package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

public class LongField extends AbstractNullableField {

    public LongField() {
        this(null);
    }

    public LongField(Boolean graphQLField) {
        super(DataType.Long, graphQLField);
    }

}
