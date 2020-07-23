package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.index.Unique;

import java.util.Set;

public class CreateUniqueMigrationOperation extends AbstractIndexMigrationOperation {

    public CreateUniqueMigrationOperation(Set<Unique> uniques) {
        super(MigrationOperationType.CREATE_UNIQUE);
        this.setIndexes(uniques);
    }

}
