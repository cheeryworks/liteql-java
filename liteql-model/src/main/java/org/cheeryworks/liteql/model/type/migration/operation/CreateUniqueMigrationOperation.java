package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.util.Set;

public class CreateUniqueMigrationOperation extends AbstractIndexMigrationOperation {

    public CreateUniqueMigrationOperation(Set<Unique> uniques) {
        super(MigrationOperationType.CREATE_UNIQUE);
        this.setIndexes(uniques);
    }

}
