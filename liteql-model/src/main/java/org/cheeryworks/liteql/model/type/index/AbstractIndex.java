package org.cheeryworks.liteql.model.type.index;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cheeryworks.liteql.model.enums.IndexType;

import java.io.Serializable;
import java.util.Set;

public abstract class AbstractIndex implements Serializable {

    @JsonIgnore
    private IndexType type;

    private Set<String> fields;

    public IndexType getType() {
        return type;
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setFields(Set<String> fields) {
        this.fields = fields;
    }

    public AbstractIndex(IndexType type) {
        this.type = type;
    }

}
