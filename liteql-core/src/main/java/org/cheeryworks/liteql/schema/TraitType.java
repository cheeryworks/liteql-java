package org.cheeryworks.liteql.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cheeryworks.liteql.schema.field.Field;

import java.util.Objects;
import java.util.Set;

public class TraitType implements Type {

    @JsonIgnore
    private TypeName typeName;

    @JsonIgnore
    private String version;

    private Set<Field> fields;

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

    public TraitType() {

    }

    public TraitType(TypeName typeName) {
        this(typeName.getSchema(), typeName.getName());
    }

    public TraitType(String schema, String typeName) {
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

        if (!(o instanceof TraitType)) {
            return false;
        }

        TraitType traitType = (TraitType) o;

        return Objects.equals(typeName, traitType.typeName) &&
                Objects.equals(fields, traitType.fields) &&
                Objects.equals(traits, traitType.traits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, fields, traits);
    }

}
