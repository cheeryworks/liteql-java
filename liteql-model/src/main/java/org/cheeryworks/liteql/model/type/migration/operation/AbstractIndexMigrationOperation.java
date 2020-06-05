package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.index.AbstractIndex;

import java.util.List;

public abstract class AbstractIndexMigrationOperation extends AbstractMigrationOperation {

    private List<? extends AbstractIndex> indexes;

    public List<? extends AbstractIndex> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<? extends AbstractIndex> indexes) {
        this.indexes = indexes;
    }

    public AbstractIndexMigrationOperation(MigrationOperationType type) {
        super(type);
    }

}
