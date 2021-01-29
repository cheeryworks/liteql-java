package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.field.DecimalField;

public class DefaultDecimalField extends AbstractNullableField implements DecimalField {

    private Integer precision;

    private Integer scale;

    @Override
    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    @Override
    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public DefaultDecimalField() {
        super();
    }

    public DefaultDecimalField(Boolean graphQLField) {
        super(graphQLField);
    }

}
