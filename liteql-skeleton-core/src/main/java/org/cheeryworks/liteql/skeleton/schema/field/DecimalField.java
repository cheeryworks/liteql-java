package org.cheeryworks.liteql.skeleton.schema.field;

public interface DecimalField extends NullableField {
    Integer getPrecision();

    Integer getScale();
}
