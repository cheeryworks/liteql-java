package org.cheeryworks.liteql.query.read;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.PublicQuery;

import java.util.LinkedList;

public class SingleReadQuery extends AbstractTypedReadQuery<SingleReadQuery> implements PublicQuery {

    public SingleReadQuery() {

    }

    public SingleReadQuery(ReadQuery readQuery) {
        setDomainTypeName(readQuery.getDomainTypeName());
        setFields(readQuery.getFields());
        setJoins(readQuery.getJoins());
        setConditions(readQuery.getConditions());
        setSorts(readQuery.getSorts());
        setReferences(readQuery.getReferences());

        if (CollectionUtils.isNotEmpty(readQuery.getAssociations())) {
            setAssociations(new LinkedList<>());

            for (ReadQuery associatedReadQuery : readQuery.getAssociations()) {
                getAssociations().add(new SingleReadQuery(associatedReadQuery));
            }
        }
    }

    public QueryType getQueryType() {
        return QueryType.SingleRead;
    }

}
