package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.index.UniqueDefinition;

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
