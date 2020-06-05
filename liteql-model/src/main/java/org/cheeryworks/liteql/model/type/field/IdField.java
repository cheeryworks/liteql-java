package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class IdField extends AbstractField {

    public IdField() {
        this.setType(DataType.Id.name().toLowerCase());
    }

}
