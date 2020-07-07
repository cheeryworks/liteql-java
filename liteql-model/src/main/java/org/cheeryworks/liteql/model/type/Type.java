package org.cheeryworks.liteql.model.type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Objects;

public class Type implements Serializable {

    @JsonIgnore
    private String schema;

    @JsonIgnore
    private String name;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getFullname() {
        if (schema != null && schema.trim().length() > 0) {
            return schema + "." + name;
        }

        return name;
    }

    public Type() {

    }

    public Type(String schema, String name) {
        this.schema = schema;
        this.name = name;
    }

    public boolean isStruct() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Type)) {
            return false;
        }

        Type that = (Type) o;

        return Objects.equals(schema, that.schema) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schema, name);
    }

}
