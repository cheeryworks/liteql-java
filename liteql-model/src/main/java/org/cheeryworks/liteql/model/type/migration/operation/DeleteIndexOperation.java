package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.type.DomainTypeIndexKey;
import org.cheeryworks.liteql.model.type.migration.MigrationOperation;

import java.util.List;

public class DeleteIndexOperation extends MigrationOperation {

    private List<DomainTypeIndexKey> indexes;

    public List<DomainTypeIndexKey> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<DomainTypeIndexKey> indexes) {
        this.indexes = indexes;
    }

    public DeleteIndexOperation() {
        this.setOperation(StandardMigrationOperationType.DELETE_INDEX.name());
    }

}
