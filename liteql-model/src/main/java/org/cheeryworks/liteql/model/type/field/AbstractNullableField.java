package org.cheeryworks.liteql.model.type.field;

import com.fasterxml.jackson.annotation.JsonGetter;

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

}
