package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class TimestampField extends AbstractNullableField {

    public TimestampField() {
        super(DataType.Timestamp.name().toLowerCase());
    }

}
