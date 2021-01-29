package org.cheeryworks.liteql.skeleton.schema.field.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.skeleton.schema.field.Field;

public abstract class AbstractField implements Field {

    private String name;

    @JsonProperty
    private Boolean graphQLField;

    protected AbstractField() {
        this(null);
    }

    protected AbstractField(Boolean graphQLField) {
        this.graphQLField = graphQLField;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isGraphQLField() {
        if (graphQLField == null) {
            return true;
        }

        return graphQLField.booleanValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractField)) {
            return false;
        }

        AbstractField that = (AbstractField) o;

        if (!name.equals(that.name)) {
            return false;
        }

        return getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }

}
