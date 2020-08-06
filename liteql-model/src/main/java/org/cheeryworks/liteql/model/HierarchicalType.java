package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.schema.annotation.LiteQLType;
import org.cheeryworks.liteql.util.LiteQL;

@LiteQLType(schema = LiteQL.Constants.SCHEMA, version = LiteQL.Constants.SPECIFICATION_VERSION)
public interface HierarchicalType extends SortableType {

    @LiteQLFieldPosition(1)
    String getParentId();

    @LiteQLFieldPosition(2)
    boolean isLeaf();

}
