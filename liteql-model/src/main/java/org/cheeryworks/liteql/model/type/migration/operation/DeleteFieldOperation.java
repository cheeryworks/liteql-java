package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.type.migration.MigrationOperation;

import java.util.List;

public class DeleteFieldOperation extends MigrationOperation {

    private List<String> fields;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public DeleteFieldOperation() {
        this.setOperation(StandardMigrationOperationType.DELETE_FIELD.name());
    }

}
