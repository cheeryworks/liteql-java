package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface LongField extends NullableField {

    @Override
    default DataType getType() {
        return DataType.Blob;
    }

}
