package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.model.query.field.QueryFieldDefinitions;
import org.cheeryworks.liteql.model.query.join.JoinedQuery;

import java.util.LinkedList;

public abstract class AbstractReadQuery extends AbstractConditionalQuery {

    private QueryFieldDefinitions fields;

    public QueryFieldDefinitions getFields() {
        return fields;
    }

    public void setFields(QueryFieldDefinitions fields) {
        this.fields = fields;
    }

    private LinkedList<JoinedQuery> joins;

    public LinkedList<JoinedQuery> getJoins() {
        return joins;
    }

    public void setJoins(LinkedList<JoinedQuery> joins) {
        this.joins = joins;
    }

}
