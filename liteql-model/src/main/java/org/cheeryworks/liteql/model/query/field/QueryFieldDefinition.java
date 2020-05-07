package org.cheeryworks.liteql.model.query.field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class QueryFieldDefinition implements Serializable {

    private String name;

    private String alias;

    private String description;

    private Boolean exportable;

    private Integer priority;

    @JsonIgnore
    private Class type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        if (StringUtils.isBlank(alias)) {
            return name;
        }

        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        if (StringUtils.isBlank(description)) {
            if (StringUtils.isBlank(alias)) {
                return name;
            } else {
                return alias;
            }
        }

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isExportable() {
        return exportable;
    }

    public void setExportable(Boolean exportable) {
        this.exportable = exportable;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public QueryFieldDefinition() {

    }

    public QueryFieldDefinition(String name, String alias, String description) {
        this.name = name;
        this.alias = alias;
        this.description = description;
    }

    public QueryFieldDefinition(String name, String alias, String description, Class type) {
        this.name = name;
        this.alias = alias;
        this.description = description;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QueryFieldDefinition that = (QueryFieldDefinition) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
