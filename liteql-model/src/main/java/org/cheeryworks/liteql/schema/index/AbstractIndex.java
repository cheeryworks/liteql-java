package org.cheeryworks.liteql.schema.index;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cheeryworks.liteql.schema.enums.IndexType;

import java.io.Serializable;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AbstractIndex)) {
            return false;
        }

        AbstractIndex that = (AbstractIndex) o;

        if (type != that.type) {
            return false;
        }

        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }

}
