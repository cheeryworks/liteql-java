package org.cheeryworks.liteql.model.migration.operation;

import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.migration.MigrationOperation;
import org.cheeryworks.liteql.model.type.DomainTypeUniqueKey;

import java.util.List;

public class CreateUniqueOperation extends MigrationOperation {

    private List<DomainTypeUniqueKey> uniques;

    public List<DomainTypeUniqueKey> getUniques() {
        return uniques;
    }

    public void setUniques(List<DomainTypeUniqueKey> uniques) {
        this.uniques = uniques;
    }

    public CreateUniqueOperation() {
        this.setOperation(StandardMigrationOperationType.CREATE_UNIQUE.name());
    }

}
