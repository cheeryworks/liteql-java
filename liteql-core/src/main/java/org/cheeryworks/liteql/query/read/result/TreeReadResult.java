package org.cheeryworks.liteql.query.read.result;

import java.util.LinkedList;
import java.util.Map;

public class TreeReadResult extends ReadResult {

    public static final String PARENT_ID_FIELD_NAME = "parentId";

    public static final String LEAF_FIELD_NAME = "leaf";

    public static final String ROOT_PARENT_ID = "ROOT";

    public static final String CHILDREN_FIELD_NAME = "children";

    public static final String SORT_CODE_FIELD_NAME = "sortCode";

    public static final String PRIORITY_FIELD_NAME = "priority";

    private LinkedList<TreeReadResult> children;

    public TreeReadResult(Map<String, Object> source) {
        super(source);
    }

    public LinkedList<TreeReadResult> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<TreeReadResult> children) {
        this.children = children;

        this.put(CHILDREN_FIELD_NAME, children);
    }

}
