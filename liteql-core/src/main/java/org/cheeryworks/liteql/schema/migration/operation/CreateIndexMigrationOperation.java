package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.index.IndexDefinition;

import java.util.Set;

public class CreateIndexMigrationOperation extends AbstractIndexMigrationOperation<IndexDefinition> {

    public CreateIndexMigrationOperation() {
        this(null);
    }

    public CreateIndexMigrationOperation(Set<IndexDefinition> indexes) {
        super(MigrationOperationType.CREATE_INDEX);
        this.setIndexes(indexes);
    }

}
