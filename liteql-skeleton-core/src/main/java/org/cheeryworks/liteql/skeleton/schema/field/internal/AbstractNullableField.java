package org.cheeryworks.liteql.skeleton.schema.field.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.skeleton.schema.field.NullableField;

public abstract class AbstractNullableField extends AbstractField implements NullableField {

    @JsonProperty
    private Boolean nullable;

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

    protected AbstractNullableField() {
        super();
    }

    protected AbstractNullableField(Boolean graphQLField) {
        super(graphQLField);
    }

}
