package org.cheeryworks.liteql.schema.field.internal;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.NullableField;

public abstract class AbstractNullableField extends AbstractField implements NullableField {

    private Boolean nullable;

    @JsonGetter
    private Boolean nullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public boolean isNullable() {
        if (nullable == null) {
            return true;
        }

        return nullable.booleanValue();
    }

    protected AbstractNullableField(DataType dataType) {
        super(dataType);
    }

    protected AbstractNullableField(DataType dataType, Boolean graphQLField) {
        super(dataType, graphQLField);
    }

}
