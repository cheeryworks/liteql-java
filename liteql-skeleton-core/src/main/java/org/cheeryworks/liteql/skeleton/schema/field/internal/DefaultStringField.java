package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.field.StringField;

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
        super();
    }

    public DefaultStringField(Boolean graphQLField) {
        super(graphQLField);
    }

}
