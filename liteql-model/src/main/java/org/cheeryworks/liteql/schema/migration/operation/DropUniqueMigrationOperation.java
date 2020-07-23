package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.index.Unique;

import java.util.Set;

public class DropUniqueMigrationOperation extends AbstractIndexMigrationOperation {

    public DropUniqueMigrationOperation(Set<Unique> uniques) {
        super(MigrationOperationType.DROP_UNIQUE);
        this.setIndexes(uniques);
    }

}
