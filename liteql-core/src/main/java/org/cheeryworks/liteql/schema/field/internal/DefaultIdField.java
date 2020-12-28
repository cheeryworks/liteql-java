package org.cheeryworks.liteql.schema.field.internal;

import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.IdField;

public class DefaultIdField extends AbstractField implements IdField {

    public DefaultIdField() {
        super(DataType.Id, null);
    }

}
