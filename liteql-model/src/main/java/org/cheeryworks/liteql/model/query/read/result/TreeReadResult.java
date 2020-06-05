package org.cheeryworks.liteql.model.query.read.result;

import org.cheeryworks.liteql.model.type.HierarchicalEntity;

import java.util.LinkedList;
import java.util.Map;

public class TreeReadResult extends ReadResult {

    private LinkedList<TreeReadResult> children;

    public TreeReadResult(Map<String, Object> source) {
        super(source);
    }

    public LinkedList<TreeReadResult> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<TreeReadResult> children) {
        this.children = children;

        this.put(HierarchicalEntity.CHILDREN_FIELD_NAME, children);
    }

}
