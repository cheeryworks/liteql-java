package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.index.Index;

import java.util.Set;

public class CreateIndexMigrationOperation extends AbstractIndexMigrationOperation<Index> {

    public CreateIndexMigrationOperation() {
        this(null);
    }

    public CreateIndexMigrationOperation(Set<Index> indexes) {
        super(MigrationOperationType.CREATE_INDEX);
        this.setIndexes(indexes);
    }

}
