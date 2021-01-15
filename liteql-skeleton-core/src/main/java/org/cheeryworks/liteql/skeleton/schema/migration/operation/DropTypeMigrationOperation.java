package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;

public class DropTypeMigrationOperation extends AbstractMigrationOperation {

    public DropTypeMigrationOperation() {
        super(MigrationOperationType.DROP_TYPE);
    }

}
