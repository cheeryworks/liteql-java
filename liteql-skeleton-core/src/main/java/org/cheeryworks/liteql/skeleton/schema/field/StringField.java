package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface StringField extends NullableField {

    Integer getLength();

    @Override
    default DataType getType() {
        return DataType.String;
    }

}
