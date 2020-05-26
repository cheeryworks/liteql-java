package org.cheeryworks.liteql.model.type;

import com.fasterxml.jackson.annotation.JsonGetter;

public abstract class AbstractNullableDomainTypeField extends AbstractDomainTypeField {

    private Boolean nullable;

    @JsonGetter
    private Boolean isNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean nullable() {
        if (nullable == null) {
            return Boolean.TRUE;
        }

        return nullable;
    }

}
