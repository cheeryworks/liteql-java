package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public abstract class AbstractField implements Field {

    private String name;

    private DataType type;

    protected AbstractField(DataType type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public DataType getType() {
        return type;
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
