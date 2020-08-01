package org.cheeryworks.liteql.query.read;

import org.cheeryworks.liteql.query.read.join.JoinedReadQuery;
import org.cheeryworks.liteql.query.read.sort.QuerySort;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractReadQuery<T extends AbstractReadQuery> extends JoinedReadQuery {

    private LinkedList<QuerySort> sorts;

    private LinkedHashMap<String, String> references;

    private List<T> associations;

    private String scope;

    public LinkedList<QuerySort> getSorts() {
        return sorts;
    }

    public void setSorts(LinkedList<QuerySort> sorts) {
        this.sorts = sorts;
    }

    public LinkedHashMap<String, String> getReferences() {
        return references;
    }

    public void setReferences(LinkedHashMap<String, String> references) {
        this.references = references;
    }

    public List<T> getAssociations() {
        return associations;
    }

    public void setAssociations(List<T> associations) {
        this.associations = associations;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
