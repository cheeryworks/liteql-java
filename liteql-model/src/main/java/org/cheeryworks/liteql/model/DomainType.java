package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.annotation.LiteQLType;

@LiteQLType
public interface DomainType extends TraitType {

    String getId();

}
