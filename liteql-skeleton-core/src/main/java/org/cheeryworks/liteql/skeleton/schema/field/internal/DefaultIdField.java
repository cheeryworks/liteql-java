package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;
import org.cheeryworks.liteql.skeleton.schema.field.IdField;

public class DefaultIdField extends AbstractField implements IdField {

    public DefaultIdField() {
        super(DataType.Id, null);
    }

}
