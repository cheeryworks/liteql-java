package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface BooleanField extends Field {

    @Override
    default DataType getType() {
        return DataType.Boolean;
    }

}
