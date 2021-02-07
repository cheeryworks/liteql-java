package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface StringField extends NullableField {

    int DEFAULT_LENGTH = 255;

    int MAX_LENGTH = 4000;

    Integer getLength();

    @Override
    default DataType getType() {
        return DataType.String;
    }

}
