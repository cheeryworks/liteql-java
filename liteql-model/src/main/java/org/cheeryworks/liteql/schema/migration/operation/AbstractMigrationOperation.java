package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;

public abstract class AbstractMigrationOperation implements MigrationOperation {

    private MigrationOperationType type;

    public MigrationOperationType getType() {
        return type;
    }

    public AbstractMigrationOperation(MigrationOperationType type) {
        this.type = type;
    }

}
