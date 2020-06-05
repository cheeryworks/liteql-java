package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.util.List;

public class DropUniqueMigrationOperation extends AbstractIndexMigrationOperation {

    public DropUniqueMigrationOperation(List<Unique> uniques) {
        super(MigrationOperationType.DROP_UNIQUE);
        this.setIndexes(uniques);
    }

}
