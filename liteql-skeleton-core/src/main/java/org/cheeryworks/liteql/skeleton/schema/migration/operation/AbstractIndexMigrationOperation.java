package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.skeleton.schema.index.AbstractIndexDefinition;

import java.util.Set;

public abstract class AbstractIndexMigrationOperation<T extends AbstractIndexDefinition>
        extends AbstractMigrationOperation {

    private Set<T> indexes;

    public Set<T> getIndexes() {
        return indexes;
    }

    public void setIndexes(Set<T> indexes) {
        this.indexes = indexes;
    }

    public AbstractIndexMigrationOperation(MigrationOperationType type) {
        super(type);
    }

}
