package org.cheeryworks.liteql.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cheeryworks.liteql.schema.field.Field;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class TraitTypeDefinition implements TypeDefinition {

    @JsonIgnore
    private TypeName typeName;

    @JsonIgnore
    private String version;

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<Field> fields;

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<TypeName> traits;

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

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public Set<TypeName> getTraits() {
        return traits;
    }

    public void setTraits(Set<TypeName> traits) {
        this.traits = traits;
    }

    public TraitTypeDefinition() {

    }

    public TraitTypeDefinition(TypeName typeName) {
        this(typeName.getSchema(), typeName.getName());
    }

    public TraitTypeDefinition(String schema, String typeName) {
        this.typeName = new TypeName(schema, typeName);
    }

    public boolean isTrait() {
        return true;
    }

    public boolean implement(TypeName traitTypeName) {
        if (traits != null && traits.size() > 0) {
            for (TypeName trait : traits) {
                if (trait.equals(traitTypeName)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TraitTypeDefinition)) {
            return false;
        }

        TraitTypeDefinition traitTypeDefinition = (TraitTypeDefinition) o;

        return Objects.equals(typeName, traitTypeDefinition.typeName) &&
                Objects.equals(fields, traitTypeDefinition.fields) &&
                Objects.equals(traits, traitTypeDefinition.traits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, fields, traits);
    }

}
