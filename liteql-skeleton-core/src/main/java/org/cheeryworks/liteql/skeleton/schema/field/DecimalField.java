package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface DecimalField extends NullableField {

    int DEFAULT_PRECISION = 19;

    int MAX_PRECISION = DEFAULT_PRECISION;

    int DEFAULT_SCALE = 2;

    int MIN_SCALE = DEFAULT_SCALE;

    int MAX_SCALE = 6;

    @Override
    default DataType getType() {
        return DataType.Decimal;
    }

    Integer getPrecision();

    Integer getScale();

}
