package org.cheeryworks.liteql.skeleton.query.read.field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class FieldDefinition implements Serializable {

    private String name;

    private String alias;

    private String description;

    private boolean visible = true;

    private int priority;

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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public FieldDefinition() {

    }

    public FieldDefinition(String name) {
        this(name, null, null, null);
    }

    public FieldDefinition(String name, String alias) {
        this(name, alias, null, null);
    }

    public FieldDefinition(String name, String alias, String description) {
        this(name, alias, description, null);
    }

    public FieldDefinition(String name, String alias, String description, Class type) {
        this(name, alias, description, type, 0);
    }

    public FieldDefinition(String name, String alias, String description, Class type, int priority) {
        this.name = name;
        this.alias = alias;
        this.description = description;
        this.type = type;
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldDefinition)) {
            return false;
        }

        FieldDefinition that = (FieldDefinition) o;

        if (!name.equals(that.name)) {
            return false;
        }

        return Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
}
