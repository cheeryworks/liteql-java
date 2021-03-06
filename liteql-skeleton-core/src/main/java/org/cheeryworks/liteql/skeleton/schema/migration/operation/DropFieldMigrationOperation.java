package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;

import java.util.List;

public class DropFieldMigrationOperation extends AbstractMigrationOperation {

    private List<String> fields;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public DropFieldMigrationOperation() {
        super(MigrationOperationType.DROP_FIELD);
    }

}
