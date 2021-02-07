package org.cheeryworks.liteql.skeleton.schema;

import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLTraitType;
import org.cheeryworks.liteql.skeleton.util.LiteQL;

@LiteQLTraitType(schema = LiteQL.Constants.SCHEMA)
public interface DomainType extends TraitType {

    String getId();

}
