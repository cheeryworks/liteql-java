package org.cheeryworks.liteql.model.type;

import java.io.Serializable;

public interface DomainTypeField extends Serializable {

    String ID_FIELD_NAME = "id";

    String getName();

    String getType();

}
