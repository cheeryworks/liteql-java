package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.type.migration.MigrationOperation;

public class DeleteTypeOperation extends MigrationOperation {

    public DeleteTypeOperation() {
        this.setOperation(StandardMigrationOperationType.DELETE_TYPE.name());
    }

}
