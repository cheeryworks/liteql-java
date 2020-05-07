package org.cheeryworks.liteql.model.migration.operation;

import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.migration.MigrationOperation;

public class DeleteTypeOperation extends MigrationOperation {

    public DeleteTypeOperation() {
        this.setOperation(StandardMigrationOperationType.DELETE_TYPE.name());
    }

}
