package org.cheeryworks.liteql.model.type.field;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.cheeryworks.liteql.model.enums.DataType;

public abstract class AbstractNullableField extends AbstractField {

    private Boolean nullable;

    @JsonGetter
    private Boolean nullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

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
