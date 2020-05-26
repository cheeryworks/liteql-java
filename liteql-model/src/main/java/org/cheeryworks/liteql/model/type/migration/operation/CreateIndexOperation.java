package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.type.DomainTypeIndexKey;
import org.cheeryworks.liteql.model.type.migration.MigrationOperation;

import java.util.List;

public class CreateIndexOperation extends MigrationOperation {

    private List<DomainTypeIndexKey> indexes;

    public List<DomainTypeIndexKey> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<DomainTypeIndexKey> indexes) {
        this.indexes = indexes;
    }

    public CreateIndexOperation() {
        this.setOperation(StandardMigrationOperationType.CREATE_INDEX.name());
    }

}
