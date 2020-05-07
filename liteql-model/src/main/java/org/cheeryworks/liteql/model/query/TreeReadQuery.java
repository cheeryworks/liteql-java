package org.cheeryworks.liteql.model.query;

public class TreeReadQuery extends ReadQuery {

    private Integer expandLevel;

    public Integer getExpandLevel() {
        return expandLevel;
    }

    public void setExpandLevel(Integer expandLevel) {
        this.expandLevel = expandLevel;
    }

}
