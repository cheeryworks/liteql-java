package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.index.Index;

import java.util.Set;

public class DropIndexMigrationOperation extends AbstractIndexMigrationOperation<Index> {

    public DropIndexMigrationOperation() {
        this(null);
    }

    public DropIndexMigrationOperation(Set<Index> indexes) {
        super(MigrationOperationType.DROP_INDEX);
        this.setIndexes(indexes);
    }

}
