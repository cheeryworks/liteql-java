package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.type.field.Field;

import java.util.Set;

public class TraitType extends Type {

    private Set<Field> fields;

    private Set<Type> traits;

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public Set<Type> getTraits() {
        return traits;
    }

    public void setTraits(Set<Type> traits) {
        this.traits = traits;
    }

    public TraitType() {

    }

    public TraitType(Type type) {
        this(type.getSchema(), type.getName());
    }

    public TraitType(String schema, String name) {
        super(schema, name);
    }

    public boolean isTrait() {
        return true;
    }

    public boolean implement(String name) {
        if (traits != null && traits.size() > 0) {
            for (Type trait : traits) {
                if (trait.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

}
