package org.cheeryworks.liteql.model.type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Objects;

public class TypeName implements Serializable {

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

    public TypeName() {

    }

    public TypeName(String schema, String name) {
        this.schema = schema;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TypeName)) {
            return false;
        }

        TypeName that = (TypeName) o;

        return Objects.equals(schema, that.schema) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schema, name);
    }

}
