package org.cheeryworks.liteql.model.query.read;

import org.cheeryworks.liteql.model.query.read.join.JoinedReadQuery;
import org.cheeryworks.liteql.model.query.read.sort.QuerySort;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractReadQuery<T extends AbstractReadQuery> extends JoinedReadQuery {

    public static final String MAIN_TABLE_ALIAS = "a";

    public static final String JOINED_TABLE_ALIAS_PREFIX = "j";

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
