package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.skeleton.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.join.JoinedReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.sort.QuerySort;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.join.ReadQueryJoinMetadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReadQueryMetadata extends AbstractReadQueryMetadata {

    private List<QuerySort> sorts = new ArrayList<>();

    private Integer expandLevel;

    private Integer page;

    private Integer size;

    private String scope;

    private LinkedHashMap<String, String> references = new LinkedHashMap<>();

    private AbstractReadQuery[] associations;

    public List<QuerySort> getSorts() {
        return sorts;
    }

    public void setSorts(List<QuerySort> sorts) {
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

    public JoinedReadQuery getJoinedQuery(ReadQueryJoinMetadata readQueryJoinMetadata) {
        JoinedReadQuery joinedReadQuery = new JoinedReadQuery();

        joinedReadQuery.setDomainTypeName(readQueryJoinMetadata.getDomainTypeName());
        joinedReadQuery.setFields(readQueryJoinMetadata.getFields());
        joinedReadQuery.setJoinConditions(readQueryJoinMetadata.getJoinConditions());
        joinedReadQuery.setConditions(readQueryJoinMetadata.getConditions());

        if (ArrayUtils.isNotEmpty(readQueryJoinMetadata.getReadQueryJoinMetadataArray())) {
            joinedReadQuery.setJoins(new ArrayList<>());

            for (ReadQueryJoinMetadata childReadQueryJoinMetadata
                    : readQueryJoinMetadata.getReadQueryJoinMetadataArray()) {
                joinedReadQuery.getJoins().add(getJoinedQuery(childReadQueryJoinMetadata));
            }
        }

        return joinedReadQuery;
    }

}
