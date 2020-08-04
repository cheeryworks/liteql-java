package org.cheeryworks.liteql.query.read.sort;


import org.cheeryworks.liteql.query.enums.Direction;

import java.io.Serializable;

public class QuerySort implements Serializable {

    private String field;

    private Direction direction = Direction.ASC;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public QuerySort() {

    }

    public QuerySort(String field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

}
