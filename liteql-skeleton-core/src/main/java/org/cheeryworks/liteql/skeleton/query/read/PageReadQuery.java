package org.cheeryworks.liteql.skeleton.query.read;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.query.read.result.PageReadResults;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;

import java.util.LinkedList;

public class PageReadQuery extends AbstractTypedReadQuery<PageReadQuery, PageReadResults> implements PublicQuery {

    private Integer page;

    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public PageReadQuery() {

    }

    public PageReadQuery(ReadQuery readQuery, Integer page, Integer size) {
        setDomainTypeName(readQuery.getDomainTypeName());
        setFields(readQuery.getFields());
        setJoins(readQuery.getJoins());
        setConditions(readQuery.getConditions());
        setSorts(readQuery.getSorts());
        setReferences(readQuery.getReferences());

        if (CollectionUtils.isNotEmpty(readQuery.getAssociations())) {
            setAssociations(new LinkedList<>());

            for (ReadQuery associatedReadQuery : readQuery.getAssociations()) {
                getAssociations().add(new PageReadQuery(associatedReadQuery, page, size));
            }
        }

        setPage(page);
        setSize(size);
    }

    @Override
    public QueryType getQueryType() {
        return QueryType.PageRead;
    }

    @Override
    public PageReadResults getResult(ReadResults readResults) {
        return new PageReadResults(
                readResults,
                getPage(),
                getSize(),
                readResults.getTotal());
    }

}
