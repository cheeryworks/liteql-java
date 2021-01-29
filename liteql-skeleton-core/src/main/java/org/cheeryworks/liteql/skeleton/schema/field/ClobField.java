package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface ClobField extends NullableField {

    @Override
    default DataType getType() {
        return DataType.Clob;
    }

}
