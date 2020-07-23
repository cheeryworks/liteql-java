package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.ResourceDefinition;
import org.cheeryworks.liteql.util.LiteQLConstants;

@ResourceDefinition(schema = LiteQLConstants.SCHEMA)
public interface HierarchicalEntity<T> extends SortableEntity {

    String PARENT_ID_FIELD_NAME = "parentId";

    String LEAF_FIELD_NAME = "leaf";

    String CHILDREN_FIELD_NAME = "children";

    String ROOT_PARENT_ID = "ROOT";

    @Position(1)
    String getParentId();

    @Position(2)
    boolean isLeaf();

}
