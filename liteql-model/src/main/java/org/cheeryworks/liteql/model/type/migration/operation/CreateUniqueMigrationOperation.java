package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.util.List;

public class CreateUniqueMigrationOperation extends AbstractIndexMigrationOperation {

    public CreateUniqueMigrationOperation(List<Unique> uniques) {
        super(MigrationOperationType.CREATE_UNIQUE);
        this.setIndexes(uniques);
    }

}
