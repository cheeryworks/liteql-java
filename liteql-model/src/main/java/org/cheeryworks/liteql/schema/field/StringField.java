package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

public class StringField extends AbstractNullableField {

    private Integer length;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public StringField() {
        this(null);
    }

    public StringField(Boolean graphQLField) {
        super(DataType.String, graphQLField);
    }

}
