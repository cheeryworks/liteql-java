package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class IdField extends AbstractField {

    public static final String ID_FIELD_NAME = "id";

    public static final int ID_FIELD_LENGTH = 128;

    public IdField() {
        super(DataType.String, true);
    }

    public int getLength() {
        return ID_FIELD_LENGTH;
    }

    public boolean isNullable() {
        return false;
    }

}
