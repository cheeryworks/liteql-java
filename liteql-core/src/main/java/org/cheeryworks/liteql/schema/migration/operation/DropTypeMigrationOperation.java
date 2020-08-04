package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;

public class DropTypeMigrationOperation extends AbstractMigrationOperation {

    public DropTypeMigrationOperation() {
        super(MigrationOperationType.DROP_TYPE);
    }

    @Override
    public void merge(DomainType domainType) {
        domainType.setDropped(true);
    }

}
