package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface IdField extends Field {

    String ID_FIELD_NAME = "id";

    int ID_FIELD_LENGTH = 128;

    @Override
    default DataType getType() {
        return DataType.Id;
    }

}
