package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.index.Index;

import java.util.List;

public class CreateIndexMigrationOperation extends AbstractIndexMigrationOperation {

    public CreateIndexMigrationOperation(List<Index> indexes) {
        super(MigrationOperationType.CREATE_INDEX);
        this.setIndexes(indexes);
    }

}
