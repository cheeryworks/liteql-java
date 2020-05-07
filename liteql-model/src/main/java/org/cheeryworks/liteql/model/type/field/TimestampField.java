package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractDomainTypeField;

public class TimestampField extends AbstractDomainTypeField {

    private Boolean nullable;

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public TimestampField() {
        this.setType(DataType.Timestamp.name().toLowerCase());
    }

}
