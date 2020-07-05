package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.type.field.Field;

import java.util.Set;

public class StructType extends TypeName {

    private Set<Field> fields;

    private Set<TypeName> structs;

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public Set<TypeName> getStructs() {
        return structs;
    }

    public void setStructs(Set<TypeName> structs) {
        this.structs = structs;
    }

    public StructType() {

    }

    public StructType(TypeName structTypeName) {
        super(structTypeName.getSchema(), structTypeName.getName());
    }

    public StructType(String schema, String name) {
        super(schema, name);
    }

    @Override
    public boolean isStruct() {
        return true;
    }

    public boolean implement(String name) {
        if (structs != null && structs.size() > 0) {
            for (TypeName struct : structs) {
                if (struct.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

}
