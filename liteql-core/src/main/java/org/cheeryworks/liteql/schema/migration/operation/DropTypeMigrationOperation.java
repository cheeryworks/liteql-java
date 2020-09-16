package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;

public class DropTypeMigrationOperation extends AbstractMigrationOperation {

    public DropTypeMigrationOperation() {
        super(MigrationOperationType.DROP_TYPE);
    }

}
