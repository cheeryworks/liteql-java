package org.cheeryworks.liteql.util.builder.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.DomainType;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LiteQLSaveQuery<T extends AbstractSaveQuery> {

    private T saveQuery;

    private DomainType domainType;

    private Map<String, Object> data = new LinkedHashMap<>();

    private Map<String, String> references = new LinkedHashMap<>();

    private LiteQLSaveQuery<T>[] associations;

    public T getSaveQuery() {
        return saveQuery;
    }

    public DomainType getDomainType() {
        return domainType;
    }

    public void setDomainType(DomainType domainType) {
        this.domainType = domainType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, String> getReferences() {
        return references;
    }

    public void setReferences(Map<String, String> references) {
        this.references = references;
    }

    public LiteQLSaveQuery<T>[] getAssociations() {
        return associations;
    }

    public void setAssociations(LiteQLSaveQuery<T>[] associations) {
        this.associations = associations;
    }

    private LiteQLSaveQuery(T saveQuery) {
        this.saveQuery = saveQuery;
    }

    public static LiteQLSaveQueryFieldsBuilder<CreateQuery> create(DomainType domainType) {
        LiteQLSaveQuery<CreateQuery> liteQLSaveQuery = new LiteQLSaveQuery<>(new CreateQuery());

        liteQLSaveQuery.setDomainType(domainType);

        return new LiteQLSaveQueryFieldsBuilder<>(liteQLSaveQuery);
    }

    public static LiteQLSaveQueryFieldsBuilder<UpdateQuery> update(DomainType domainType) {
        LiteQLSaveQuery<UpdateQuery> liteQLSaveQuery = new LiteQLSaveQuery<>(new UpdateQuery());

        liteQLSaveQuery.setDomainType(domainType);

        return new LiteQLSaveQueryFieldsBuilder<>(liteQLSaveQuery);
    }

}
