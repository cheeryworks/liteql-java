package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractDomainTypeField;

public class StringField extends AbstractDomainTypeField {

    private Integer length;

    private Boolean nullable;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public StringField() {
        this.setType(DataType.String.name().toLowerCase());
    }

}
