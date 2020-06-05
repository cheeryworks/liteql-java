package org.cheeryworks.liteql.model.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cheeryworks.liteql.model.annotation.GraphQLField;

import java.util.LinkedHashSet;

public interface HierarchicalEntity<T> extends SortableEntity {

    String PARENT_ID_FIELD_NAME = "parentId";

    String CHILDREN_FIELD_NAME = "children";

    String ROOT_PARENT_ID = "ROOT";

    String getId();

    void setId(String id);

    String getParentId();

    void setParentId(String parentId);

    int getPriority();

    void setPriority(int priority);

    @GraphQLField(ignore = true)
    String getIconCls();

    void setIconCls(String iconCls);

    boolean isLeaf();

    void setLeaf(boolean leaf);

    @JsonIgnore
    @GraphQLField(ignore = true)
    boolean isChecked();

    void setChecked(boolean checked);

    @GraphQLField(ignore = true)
    boolean isExpanded();

    void setExpanded(boolean expanded);

    @GraphQLField(name = "children")
    LinkedHashSet<T> getChildren();

    void setChildren(LinkedHashSet<T> children);

    void addChild(T child);

}
