package org.cheeryworks.liteql.skeleton.query.read.field;

import java.util.LinkedHashSet;
import java.util.Objects;

public class FieldDefinitions extends LinkedHashSet<FieldDefinition> {

    public FieldDefinition getFieldDefinition(String alias) {
        return this.stream()
                .filter(fieldDefinition -> Objects.equals(fieldDefinition.getAlias(), alias))
                .findFirst().get();
    }

}
