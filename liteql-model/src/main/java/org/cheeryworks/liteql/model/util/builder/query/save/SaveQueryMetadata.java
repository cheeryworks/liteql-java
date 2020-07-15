package org.cheeryworks.liteql.model.util.builder.query.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.TypeName;

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

    private SaveQueryMetadata(T saveQuery) {
        this.saveQuery = saveQuery;
    }

    public static SaveQueryFieldsBuilder<CreateQuery> create(TypeName domainTypeName) {
        SaveQueryMetadata<CreateQuery> saveQueryMetadata = new SaveQueryMetadata<>(new CreateQuery());

        saveQueryMetadata.setDomainTypeName(domainTypeName);

        return new SaveQueryFieldsBuilder<>(saveQueryMetadata);
    }

    public static SaveQueryFieldsBuilder<UpdateQuery> update(TypeName domainTypeName) {
        SaveQueryMetadata<UpdateQuery> saveQueryMetadata = new SaveQueryMetadata<>(new UpdateQuery());

        saveQueryMetadata.setDomainTypeName(domainTypeName);

        return new SaveQueryFieldsBuilder<>(saveQueryMetadata);
    }

}
