package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;

public abstract class AbstractMigrationOperation implements MigrationOperation {

    private MigrationOperationType type;

    public MigrationOperationType getType() {
        return type;
    }

    public AbstractMigrationOperation(MigrationOperationType type) {
        this.type = type;
    }

}
