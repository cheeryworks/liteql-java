package org.cheeryworks.liteql.schema;

import java.io.Serializable;

public interface Type extends Serializable {

    TypeName getTypeName();

    void setTypeName(TypeName typeName);

    boolean isTrait();

}
