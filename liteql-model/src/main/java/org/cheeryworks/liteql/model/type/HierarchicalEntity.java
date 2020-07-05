package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.annotation.ResourceDefinition;
import org.cheeryworks.liteql.model.util.LiteQLConstants;

@ResourceDefinition(namespace = LiteQLConstants.NAMESPACE)
public interface HierarchicalEntity<T> extends SortableEntity {

    String PARENT_ID_FIELD_NAME = "parentId";

    String CHILDREN_FIELD_NAME = "children";

    String ROOT_PARENT_ID = "ROOT";

    String getParentId();

    boolean isLeaf();

}
