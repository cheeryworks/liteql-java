package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class DecimalField extends AbstractNullableField {

    private Integer precision;

    private Integer scale;

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public DecimalField() {
        this(null);
    }

    public DecimalField(Boolean graphQLField) {
        super(DataType.Decimal, graphQLField);
    }

}
