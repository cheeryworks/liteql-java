package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.skeleton.schema.index.UniqueDefinition;

import java.util.Set;

public class DropUniqueMigrationOperation extends AbstractIndexMigrationOperation<UniqueDefinition> {

    public DropUniqueMigrationOperation() {
        this(null);
    }

    public DropUniqueMigrationOperation(Set<UniqueDefinition> uniqueDefinitions) {
        super(MigrationOperationType.DROP_UNIQUE);
        this.setIndexes(uniqueDefinitions);
    }

}
