package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface TimestampField extends NullableField {

    @Override
    default DataType getType() {
        return DataType.Timestamp;
    }

}
