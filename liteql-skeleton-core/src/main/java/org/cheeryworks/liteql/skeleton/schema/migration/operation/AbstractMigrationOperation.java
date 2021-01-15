package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;

public abstract class AbstractMigrationOperation implements MigrationOperation {

    private MigrationOperationType type;

    @Override
    public MigrationOperationType getType() {
        return type;
    }

    public AbstractMigrationOperation(MigrationOperationType type) {
        this.type = type;
    }

}
