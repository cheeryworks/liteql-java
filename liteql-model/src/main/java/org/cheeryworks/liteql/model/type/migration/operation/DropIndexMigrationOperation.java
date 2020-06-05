package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.index.Index;

import java.util.List;

public class DropIndexMigrationOperation extends AbstractIndexMigrationOperation {

    public DropIndexMigrationOperation(List<Index> indexes) {
        super(MigrationOperationType.DROP_INDEX);
        this.setIndexes(indexes);
    }

}
