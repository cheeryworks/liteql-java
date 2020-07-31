package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.Position;

public interface SortableEntity extends Entity {

    String SORT_CODE_FIELD_NAME = "sortCode";

    String PRIORITY_FIELD_NAME = "priority";

    @Position(1)
    String getSortCode();

    @Position(2)
    int getPriority();

}
