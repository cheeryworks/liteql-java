package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractDomainTypeField;

public class ClobField extends AbstractDomainTypeField {

    public ClobField() {
        this.setType(DataType.Clob.name().toLowerCase());
    }

}
