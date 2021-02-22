package org.cheeryworks.liteql.skeleton.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cheeryworks.liteql.skeleton.schema.field.Field;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractTypeDefinition implements TypeDefinition {

    @JsonIgnore
    private TypeName typeName;

    @JsonIgnore
    private String version;

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<Field> fields;

    @Override
    public TypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public AbstractTypeDefinition() {

    }

    public AbstractTypeDefinition(TypeName typeName) {
        this(typeName.getSchema(), typeName.getName());
    }

    public AbstractTypeDefinition(String schema, String typeName) {
        this.setTypeName(new TypeName(schema, typeName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractTypeDefinition that = (AbstractTypeDefinition) o;

        return Objects.equals(typeName, that.typeName) &&
                Objects.equals(version, that.version) &&
                Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, version, fields);
    }

}
