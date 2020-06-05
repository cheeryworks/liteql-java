package org.cheeryworks.liteql.model.query.read.join;

import org.cheeryworks.liteql.model.query.read.AbstractFieldReadQuery;

import java.util.LinkedList;

public class JoinedReadQuery extends AbstractFieldReadQuery {

    private LinkedList<JoinedReadQuery> joins;

    public LinkedList<JoinedReadQuery> getJoins() {
        return joins;
    }

    public void setJoins(LinkedList<JoinedReadQuery> joins) {
        this.joins = joins;
    }

}
