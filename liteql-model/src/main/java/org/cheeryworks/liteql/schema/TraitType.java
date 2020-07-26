package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.schema.field.Field;

import java.util.Set;

public class TraitType implements Type {

    private TypeName typeName;

    private Set<Field> fields;

    private Set<TypeName> traits;

    @Override
    public TypeName getTypeName() {
        return typeName;
    }

    @Override
    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
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

}
