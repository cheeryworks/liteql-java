package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.field.Field;

import java.util.Set;

public class CreateFieldMigrationOperation extends AbstractMigrationOperation {

    private Set<Field> fields;

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public CreateFieldMigrationOperation() {
        super(MigrationOperationType.CREATE_FIELD);
    }

}
