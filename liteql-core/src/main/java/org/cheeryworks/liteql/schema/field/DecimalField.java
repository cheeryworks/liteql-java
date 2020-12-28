package org.cheeryworks.liteql.schema.field;

public interface DecimalField extends NullableField {
    Integer getPrecision();

    Integer getScale();
}
