package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.Type;

@Type
public interface HierarchicalEntity<T> extends SortableEntity {

    @Position(1)
    String getParentId();

    @Position(2)
    boolean isLeaf();

}
