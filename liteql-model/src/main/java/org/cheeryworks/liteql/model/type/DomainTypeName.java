package org.cheeryworks.liteql.model.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize
public class DomainTypeName implements Serializable {

    public static final String DOMAIN_TYPE_NAME_KEY = "domainTypeName";

    private String schema;

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
        return schema + "." + name;
    }

    public DomainTypeName() {

    }

    public DomainTypeName(String schema, String name) {
        this.schema = schema;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DomainTypeName)) {
            return false;
        }

        DomainTypeName that = (DomainTypeName) o;

        if (!schema.equals(that.schema)) {
            return false;
        }

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = schema.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
