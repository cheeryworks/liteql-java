package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;

public class DropTypeMigrationOperation extends AbstractMigrationOperation {

    public DropTypeMigrationOperation() {
        super(MigrationOperationType.DROP_TYPE);
    }

}
