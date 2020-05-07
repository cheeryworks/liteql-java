package org.cheeryworks.liteql.model.query;

import java.util.List;
import java.util.Map;

public class SaveQuery extends AbstractQuery {

    private Map<String, Object> data;

    private Map<String, String> references;

    private List<SaveQuery> associations;

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

    public List<SaveQuery> getAssociations() {
        return associations;
    }

    public void setAssociations(List<SaveQuery> associations) {
        this.associations = associations;
    }

}
