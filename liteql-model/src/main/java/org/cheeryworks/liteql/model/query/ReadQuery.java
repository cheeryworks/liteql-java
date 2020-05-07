package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.model.query.sort.QuerySort;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReadQuery extends AbstractReadQuery {

    private LinkedList<QuerySort> sorts;

    private LinkedHashMap<String, String> references;

    private LinkedList<ReadQuery> associations;

    public LinkedList<QuerySort> getSorts() {
        return sorts;
    }

    public void setSorts(LinkedList<QuerySort> sorts) {
        this.sorts = sorts;
    }

    public Map<String, String> getReferences() {
        return references;
    }

    public void setReferences(LinkedHashMap<String, String> references) {
        this.references = references;
    }

    public List<ReadQuery> getAssociations() {
        return associations;
    }

    public void setAssociations(LinkedList<ReadQuery> associations) {
        this.associations = associations;
    }

}
