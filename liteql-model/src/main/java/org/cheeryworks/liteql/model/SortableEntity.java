package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.Type;

@Type
public interface SortableEntity extends Entity {

    @Position(1)
    String getSortCode();

    @Position(2)
    int getPriority();

}
