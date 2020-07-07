package org.cheeryworks.liteql.util.builder.read;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.model.query.read.join.JoinedReadQuery;
import org.cheeryworks.liteql.model.query.read.sort.QuerySort;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.util.builder.read.join.LiteQLReadQueryJoin;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class LiteQLReadQuery extends AbstractLiteQLReadQuery {

    private LinkedList<QuerySort> sorts = new LinkedList<>();

    private Integer expandLevel;

    private Integer page;

    private Integer size;

    private String scope;

    private LinkedHashMap<String, String> references = new LinkedHashMap<>();

    private AbstractReadQuery[] associations;

    public LinkedList<QuerySort> getSorts() {
        return sorts;
    }

    public void setSorts(LinkedList<QuerySort> sorts) {
        this.sorts = sorts;
    }

    public Integer getExpandLevel() {
        return expandLevel;
    }

    public void setExpandLevel(Integer expandLevel) {
        this.expandLevel = expandLevel;
    }

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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public LinkedHashMap<String, String> getReferences() {
        return references;
    }

    public void setReferences(LinkedHashMap<String, String> references) {
        this.references = references;
    }

    public AbstractReadQuery[] getAssociations() {
        return associations;
    }

    public void setAssociations(AbstractReadQuery[] associations) {
        this.associations = associations;
    }

    public JoinedReadQuery getJoinedQuery(LiteQLReadQueryJoin liteQLReadQueryJoin) {
        JoinedReadQuery joinedReadQuery = new JoinedReadQuery();

        joinedReadQuery.setDomainType(liteQLReadQueryJoin.getDomainType());
        joinedReadQuery.setFields(liteQLReadQueryJoin.getFields());
        joinedReadQuery.setConditions(liteQLReadQueryJoin.getConditions());

        if (ArrayUtils.isNotEmpty(liteQLReadQueryJoin.getLiteQLReadQueryJoins())) {
            joinedReadQuery.setJoins(new LinkedList<>());

            for (LiteQLReadQueryJoin childLiteQLReadQueryJoin : liteQLReadQueryJoin.getLiteQLReadQueryJoins()) {
                joinedReadQuery.getJoins().add(getJoinedQuery(childLiteQLReadQueryJoin));
            }
        }

        return joinedReadQuery;
    }

    public static LiteQLReadQueryFieldsBuilder read(DomainType domainType) {
        LiteQLReadQuery liteQLReadQuery = new LiteQLReadQuery();

        liteQLReadQuery.setDomainType(domainType);

        return new LiteQLReadQueryFieldsBuilder(liteQLReadQuery);
    }

}
