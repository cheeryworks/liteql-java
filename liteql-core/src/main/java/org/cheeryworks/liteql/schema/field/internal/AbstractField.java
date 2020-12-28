package org.cheeryworks.liteql.schema.field.internal;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.Field;

public abstract class AbstractField implements Field {

    private String name;

    private DataType type;

    private Boolean graphQLField;

    protected AbstractField(DataType dataType) {
        this(dataType, null);
    }

    protected AbstractField(DataType dataType, Boolean graphQLField) {
        this.type = dataType;
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
    public DataType getType() {
        return type;
    }

    @JsonGetter
    private Boolean graphQLField() {
        return graphQLField;
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

        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

}
