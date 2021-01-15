package org.cheeryworks.liteql.skeleton.model;

import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLTraitType;

@LiteQLTraitType
public interface SortableType extends DomainType {

    @LiteQLFieldPosition(1)
    String getSortCode();

    @LiteQLFieldPosition(2)
    int getPriority();

}
