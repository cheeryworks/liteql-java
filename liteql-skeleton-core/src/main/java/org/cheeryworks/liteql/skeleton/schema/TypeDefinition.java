package org.cheeryworks.liteql.skeleton.schema;

import org.cheeryworks.liteql.skeleton.schema.field.Field;

import java.io.Serializable;
import java.util.Set;

public interface TypeDefinition extends Serializable {

    String TRAIT_NAME_KEY = "trait";

    TypeName getTypeName();

    String getVersion();

    Set<Field> getFields();

    boolean isTrait();

}
