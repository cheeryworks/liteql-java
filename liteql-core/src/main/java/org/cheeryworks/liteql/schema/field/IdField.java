package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

public class IdField extends AbstractField {

    public static final String ID_FIELD_NAME = "id";

    public static final int ID_FIELD_LENGTH = 128;

    public IdField() {
        super(DataType.Id, null);
    }

}
