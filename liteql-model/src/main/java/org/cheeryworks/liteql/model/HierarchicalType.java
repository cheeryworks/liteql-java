package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.schema.annotation.LiteQLTraitType;

@LiteQLTraitType
public interface HierarchicalType extends SortableType {

    @LiteQLFieldPosition(1)
    String getParentId();

    @LiteQLFieldPosition(2)
    boolean isLeaf();

}
