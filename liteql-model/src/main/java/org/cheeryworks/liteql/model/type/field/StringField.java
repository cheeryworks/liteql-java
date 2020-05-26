package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractNullableDomainTypeField;

public class StringField extends AbstractNullableDomainTypeField {

    private Integer length;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public StringField() {
        this.setType(DataType.String.name().toLowerCase());
    }

}
