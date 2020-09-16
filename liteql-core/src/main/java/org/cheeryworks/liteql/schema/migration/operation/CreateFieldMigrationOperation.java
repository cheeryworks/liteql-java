package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.field.Field;

import java.util.Set;

public class CreateFieldMigrationOperation extends AbstractMigrationOperation {

    private Set<Field> fields;

    public Set<Field> getFields() {
        return fields;
    }

    public CreateFieldMigrationOperation() {
        this(null);
    }

    public CreateFieldMigrationOperation(Set<Field> fields) {
        super(MigrationOperationType.CREATE_FIELD);

        this.fields = fields;
    }

}
