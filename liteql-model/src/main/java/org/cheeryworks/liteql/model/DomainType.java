package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.annotation.LiteQLType;
import org.cheeryworks.liteql.util.LiteQL;

@LiteQLType(schema = LiteQL.Constants.SCHEMA, version = LiteQL.Constants.SPECIFICATION_VERSION)
public interface DomainType extends TraitType {

    String getId();

}
