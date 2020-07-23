package org.cheeryworks.liteql.util.query.builder.save;

import org.cheeryworks.liteql.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.schema.TypeName;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SaveQueryMetadata<T extends AbstractSaveQuery> {

    private T saveQuery;

    private TypeName domainTypeName;

    private Map<String, Object> data = new LinkedHashMap<>();

    private Map<String, String> references = new LinkedHashMap<>();

    private SaveQueryMetadata<T>[] associations;

    public T getSaveQuery() {
        return saveQuery;
    }

    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
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

    public SaveQueryMetadata<T>[] getAssociations() {
        return associations;
    }

    public void setAssociations(SaveQueryMetadata<T>[] associations) {
        this.associations = associations;
    }

    public SaveQueryMetadata(T saveQuery) {
        this.saveQuery = saveQuery;
    }

}
