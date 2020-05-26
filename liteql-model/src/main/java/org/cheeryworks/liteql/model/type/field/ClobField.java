package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractNullableDomainTypeField;

public class ClobField extends AbstractNullableDomainTypeField {

    public ClobField() {
        this.setType(DataType.Clob.name().toLowerCase());
    }

}
