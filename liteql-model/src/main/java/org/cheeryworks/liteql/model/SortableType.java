package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.schema.annotation.LiteQLTraitType;

@LiteQLTraitType
public interface SortableType extends DomainType {

    @LiteQLFieldPosition(1)
    String getSortCode();

    @LiteQLFieldPosition(2)
    int getPriority();

}
