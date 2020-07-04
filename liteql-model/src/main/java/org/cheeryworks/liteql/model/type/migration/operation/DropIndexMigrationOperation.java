package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.index.Index;

import java.util.Set;

public class DropIndexMigrationOperation extends AbstractIndexMigrationOperation {

    public DropIndexMigrationOperation(Set<Index> indexes) {
        super(MigrationOperationType.DROP_INDEX);
        this.setIndexes(indexes);
    }

}
