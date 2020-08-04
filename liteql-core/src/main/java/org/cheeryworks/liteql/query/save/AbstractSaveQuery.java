package org.cheeryworks.liteql.query.save;

import org.cheeryworks.liteql.query.AbstractDomainQuery;

import java.util.Map;

public abstract class AbstractSaveQuery extends AbstractDomainQuery {

    private Map<String, Object> data;

    private Map<String, String> references;

    private SaveQueryAssociations associations;

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

    public SaveQueryAssociations getAssociations() {
        return associations;
    }

    public void setAssociations(SaveQueryAssociations associations) {
        this.associations = associations;
    }

}
