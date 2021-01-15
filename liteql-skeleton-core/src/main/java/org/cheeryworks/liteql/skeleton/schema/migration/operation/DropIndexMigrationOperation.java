package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.skeleton.schema.index.IndexDefinition;

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
