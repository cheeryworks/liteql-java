package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.index.IndexDefinition;

import java.util.Set;

public class DropIndexMigrationOperation extends AbstractIndexMigrationOperation<IndexDefinition> {

    public DropIndexMigrationOperation() {
        this(null);
    }

    public DropIndexMigrationOperation(Set<IndexDefinition> indexes) {
        super(MigrationOperationType.DROP_INDEX);
        this.setIndexes(indexes);
    }

}
