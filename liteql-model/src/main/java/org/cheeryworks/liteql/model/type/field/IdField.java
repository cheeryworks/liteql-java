package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractDomainTypeField;

public class IdField extends AbstractDomainTypeField {

    public IdField() {
        this.setType(DataType.Id.name().toLowerCase());
    }

}
