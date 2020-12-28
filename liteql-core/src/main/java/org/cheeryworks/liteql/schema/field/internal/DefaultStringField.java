package org.cheeryworks.liteql.schema.field.internal;

import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.StringField;

public class DefaultStringField extends AbstractNullableField implements StringField {

    private Integer length;

    @Override
    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public DefaultStringField() {
        this(null);
    }

    public DefaultStringField(Boolean graphQLField) {
        super(DataType.String, graphQLField);
    }

}
