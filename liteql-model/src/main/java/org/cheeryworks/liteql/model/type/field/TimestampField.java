package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractNullableDomainTypeField;

public class TimestampField extends AbstractNullableDomainTypeField {

    public TimestampField() {
        this.setType(DataType.Timestamp.name().toLowerCase());
    }

}
