package org.cheeryworks.liteql.skeleton.query.read;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResult;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;

import java.util.LinkedList;

public class SingleReadQuery extends AbstractTypedReadQuery<SingleReadQuery, ReadResult> implements PublicQuery {

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

    @Override
    public QueryType getQueryType() {
        return QueryType.SingleRead;
    }

    public ReadResult getResult(ReadResults readResults) {
        if (readResults.getTotal() == 0) {
            return null;
        } else if (readResults.getTotal() > 1) {
            throw new IllegalStateException("More than one result found");
        }

        return readResults.get(0);
    }

}
