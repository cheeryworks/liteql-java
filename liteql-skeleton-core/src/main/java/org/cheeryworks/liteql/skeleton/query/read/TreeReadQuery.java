package org.cheeryworks.liteql.skeleton.query.read;

import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;
import org.cheeryworks.liteql.skeleton.query.read.result.TreeReadResults;

public class TreeReadQuery extends AbstractTypedReadQuery<ReadQuery, TreeReadResults> implements PublicQuery {

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

    @Override
    public TreeReadResults getResult(ReadResults readResults) {
        return TreeReadResults.transformInTree(readResults);
    }

}
