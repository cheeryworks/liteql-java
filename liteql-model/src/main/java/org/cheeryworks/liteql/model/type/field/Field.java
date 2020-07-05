package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

import java.io.Serializable;

public interface Field extends Serializable {

    String ID_FIELD_NAME = "id";

    String getName();

    void setName(String name);

    DataType getType();

}
