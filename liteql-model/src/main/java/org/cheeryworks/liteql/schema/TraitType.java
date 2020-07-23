package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.schema.field.Field;

import java.util.Set;

public class TraitType extends TypeName {

    private Set<Field> fields;

    private Set<TypeName> traits;

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

    public TraitType(String schema, String name) {
        super(schema, name);
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