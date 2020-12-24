package org.cheeryworks.liteql.query.read.join;

import org.cheeryworks.liteql.query.QueryCondition;
import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.query.read.AbstractFieldReadQuery;

import java.util.LinkedList;

public class JoinedReadQuery extends AbstractFieldReadQuery {

    private QueryConditions joinConditions;

    private LinkedList<JoinedReadQuery> joins;

    public QueryConditions getJoinConditions() {
        return joinConditions;
    }

    public void setJoinConditions(QueryConditions joinConditions) {
        this.joinConditions = joinConditions;
    }

    public void addJoinCondition(QueryCondition condition) {
        if (this.joinConditions == null) {
            this.joinConditions = new QueryConditions();
        }

        this.joinConditions.add(condition);
    }

    public void addJoinCondition(String field, ConditionClause condition, ConditionType type, Object value) {
        this.addJoinCondition(new QueryCondition(field, condition, type, value));
    }

    public LinkedList<JoinedReadQuery> getJoins() {
        return joins;
    }

    public void setJoins(LinkedList<JoinedReadQuery> joins) {
        this.joins = joins;
    }

}
