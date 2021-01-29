package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface IntegerField extends NullableField {

    @Override
    default DataType getType() {
        return DataType.Integer;
    }

}
