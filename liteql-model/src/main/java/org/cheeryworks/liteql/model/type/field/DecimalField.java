package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractNullableDomainTypeField;

public class DecimalField extends AbstractNullableDomainTypeField {

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
        this.setType(DataType.Decimal.name().toLowerCase());
    }

}
