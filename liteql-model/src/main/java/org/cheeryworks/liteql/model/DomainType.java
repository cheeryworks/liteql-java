package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.annotation.LiteQLTraitType;

@LiteQLTraitType
public interface DomainType extends TraitType {

    String getId();

}
