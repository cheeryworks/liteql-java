package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface DecimalField extends NullableField {

    @Override
    default DataType getType() {
        return DataType.Decimal;
    }

    Integer getPrecision();

    Integer getScale();

}
