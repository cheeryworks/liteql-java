package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.field.Field;

import java.util.List;

public class CreateFieldMigrationOperation extends AbstractMigrationOperation {

    private List<Field> fields;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public CreateFieldMigrationOperation() {
        super(MigrationOperationType.CREATE_FIELD);
    }

}
