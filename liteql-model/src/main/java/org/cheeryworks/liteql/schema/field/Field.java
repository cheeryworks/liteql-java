package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

import java.io.Serializable;

public interface Field extends Serializable {

    String getName();

    DataType getType();

    boolean isGraphQLField();

}
