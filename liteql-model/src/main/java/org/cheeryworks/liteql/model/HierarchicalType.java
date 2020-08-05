package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.schema.annotation.LiteQLType;

@LiteQLType
public interface HierarchicalType extends SortableType {

    @LiteQLFieldPosition(1)
    String getParentId();

    @LiteQLFieldPosition(2)
    boolean isLeaf();

}
