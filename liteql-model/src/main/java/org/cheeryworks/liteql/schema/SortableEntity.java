package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.ResourceDefinition;
import org.cheeryworks.liteql.util.LiteQLConstants;

@ResourceDefinition(schema = LiteQLConstants.SCHEMA)
public interface SortableEntity extends Entity {

    String SORT_CODE_FIELD_NAME = "sortCode";

    String PRIORITY_FIELD_NAME = "priority";

    @Position(1)
    String getSortCode();

    @Position(2)
    int getPriority();

}
