package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

import java.io.Serializable;

public interface Field extends Serializable {

    String getName();

    void setName(String name);

    DataType getType();

    boolean isGraphQLField();

}
