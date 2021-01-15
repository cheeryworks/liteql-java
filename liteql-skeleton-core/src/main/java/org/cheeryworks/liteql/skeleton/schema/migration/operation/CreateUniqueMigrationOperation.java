package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.skeleton.schema.index.UniqueDefinition;

import java.util.Set;

public class CreateUniqueMigrationOperation extends AbstractIndexMigrationOperation<UniqueDefinition> {

    public CreateUniqueMigrationOperation() {
        this(null);
    }

    public CreateUniqueMigrationOperation(Set<UniqueDefinition> uniqueDefinitions) {
        super(MigrationOperationType.CREATE_UNIQUE);
        this.setIndexes(uniqueDefinitions);
    }

}
