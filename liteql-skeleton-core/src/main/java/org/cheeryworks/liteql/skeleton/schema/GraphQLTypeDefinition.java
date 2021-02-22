package org.cheeryworks.liteql.skeleton.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class GraphQLTypeDefinition extends AbstractTypeDefinition {

    public static final String EXTENSION_KEY = "extension";

    private TypeName extension;

    public TypeName getExtension() {
        return extension;
    }

    public void setExtension(TypeName extension) {
        this.extension = extension;
    }

    public GraphQLTypeDefinition() {
        super();
    }

    public GraphQLTypeDefinition(TypeName typeName) {
        super(typeName.getSchema(), typeName.getName());
    }

    public GraphQLTypeDefinition(String schema, String typeName) {
        super(schema, typeName);
    }

    @Override
    @JsonIgnore
    public boolean isTrait() {
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

        GraphQLTypeDefinition that = (GraphQLTypeDefinition) o;

        return Objects.equals(extension, that.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), extension);
    }

}
