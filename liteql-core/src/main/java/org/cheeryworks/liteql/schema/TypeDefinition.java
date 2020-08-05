package org.cheeryworks.liteql.schema;

import java.io.Serializable;

public interface TypeDefinition extends Serializable {

    String TRAIT_NAME_KEY = "trait";

    TypeName getTypeName();

    String getVersion();

    boolean isTrait();

}
