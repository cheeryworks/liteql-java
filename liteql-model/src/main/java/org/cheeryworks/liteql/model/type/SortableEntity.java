package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.annotation.ResourceDefinition;
import org.cheeryworks.liteql.model.util.LiteQLConstants;

@ResourceDefinition(namespace = LiteQLConstants.NAMESPACE)
public interface SortableEntity extends Entity {

    String SORT_CODE_FIELD_NAME = "sortCode";

    String getSortCode();

    int getPriority();

}
