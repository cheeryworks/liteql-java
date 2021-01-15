package org.cheeryworks.liteql.skeleton.model;

import org.cheeryworks.liteql.skeleton.schema.TraitType;
import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLTraitType;

@LiteQLTraitType
public interface DomainType extends TraitType {

    String getId();

}
