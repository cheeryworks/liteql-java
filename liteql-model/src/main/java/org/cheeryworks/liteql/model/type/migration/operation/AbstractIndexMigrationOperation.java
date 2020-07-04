package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.index.AbstractIndex;

import java.util.Set;

public abstract class AbstractIndexMigrationOperation extends AbstractMigrationOperation {

    private Set<? extends AbstractIndex> indexes;

    public Set<? extends AbstractIndex> getIndexes() {
        return indexes;
    }

    public void setIndexes(Set<? extends AbstractIndex> indexes) {
        this.indexes = indexes;
    }

    public AbstractIndexMigrationOperation(MigrationOperationType type) {
        super(type);
    }

}
