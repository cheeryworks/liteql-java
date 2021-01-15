package org.cheeryworks.liteql.skeleton.model;

import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLTraitType;

@LiteQLTraitType
public interface HierarchicalType extends SortableType {

    @LiteQLFieldPosition(1)
    String getParentId();

    @LiteQLFieldPosition(2)
    boolean isLeaf();

}
