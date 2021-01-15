package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

import java.io.Serializable;

public interface Field extends Serializable {

    String getName();

    DataType getType();

    boolean isGraphQLField();

}
