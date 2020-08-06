package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.schema.annotation.LiteQLType;
import org.cheeryworks.liteql.util.LiteQL;

@LiteQLType(schema = LiteQL.Constants.SCHEMA, version = LiteQL.Constants.SPECIFICATION_VERSION)
public interface SortableType extends DomainType {

    @LiteQLFieldPosition(1)
    String getSortCode();

    @LiteQLFieldPosition(2)
    int getPriority();

}
