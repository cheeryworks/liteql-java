package org.cheeryworks.liteql.model.type.field;

import java.io.Serializable;

public interface Field extends Serializable {

    String ID_FIELD_NAME = "id";

    String getName();

    String getType();

}
