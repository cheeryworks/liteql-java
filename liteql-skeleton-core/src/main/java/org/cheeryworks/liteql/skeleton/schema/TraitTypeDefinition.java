package org.cheeryworks.liteql.skeleton.schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class TraitTypeDefinition extends AbstractTypeDefinition {

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<TypeName> traits;

    public Set<TypeName> getTraits() {
        return traits;
    }

    public void setTraits(Set<TypeName> traits) {
        this.traits = traits;
    }

    public TraitTypeDefinition() {
        super();
    }

    public TraitTypeDefinition(TypeName typeName) {
        super(typeName.getSchema(), typeName.getName());
    }

    public TraitTypeDefinition(String schema, String typeName) {
        super(schema, typeName);
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

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        TraitTypeDefinition that = (TraitTypeDefinition) o;

        return Objects.equals(traits, that.traits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), traits);
    }

}
