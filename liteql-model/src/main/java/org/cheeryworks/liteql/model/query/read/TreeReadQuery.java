package org.cheeryworks.liteql.model.query.read;

import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.PublicQuery;

public class TreeReadQuery extends AbstractTypedReadQuery<ReadQuery> implements PublicQuery {

    private Integer expandLevel;

    public Integer getExpandLevel() {
        return expandLevel;
    }

    public void setExpandLevel(Integer expandLevel) {
        this.expandLevel = expandLevel;
    }

    public TreeReadQuery() {

    }

    public TreeReadQuery(ReadQuery readQuery) {
        setDomainTypeName(readQuery.getDomainTypeName());
        setFields(readQuery.getFields());
        setJoins(readQuery.getJoins());
        setConditions(readQuery.getConditions());
        setSorts(readQuery.getSorts());
        setReferences(readQuery.getReferences());
        setAssociations(readQuery.getAssociations());
    }

    public QueryType getQueryType() {
        return QueryType.TreeRead;
    }

}
