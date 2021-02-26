package org.cheeryworks.liteql.skeleton.query.read.result;

import java.util.List;
import java.util.Map;

public class TreeReadResult extends ReadResult {

    public static final String PARENT_ID_FIELD_NAME = "parentId";

    public static final String LEAF_FIELD_NAME = "leaf";

    public static final String ROOT_PARENT_ID = "ROOT";

    public static final String CHILDREN_FIELD_NAME = "children";

    public static final String SORT_CODE_FIELD_NAME = "sortCode";

    public static final String PRIORITY_FIELD_NAME = "priority";

    private List<TreeReadResult> children;

    public TreeReadResult(Map<String, Object> source) {
        super(source);
    }

    public List<TreeReadResult> getChildren() {
        return children;
    }

    public void setChildren(List<TreeReadResult> children) {
        this.children = children;

        this.put(CHILDREN_FIELD_NAME, children);
    }

}
