package org.cheeryworks.liteql.model.migration.operation;

import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.migration.MigrationOperation;
import org.cheeryworks.liteql.model.type.DomainTypeField;
import org.cheeryworks.liteql.model.type.DomainTypeUniqueKey;

import java.util.List;

public class CreateTypeOperation extends MigrationOperation {

    private List<DomainTypeField> fields;

    private List<DomainTypeUniqueKey> uniques;

    public List<DomainTypeField> getFields() {
        return fields;
    }

    public void setFields(List<DomainTypeField> fields) {
        this.fields = fields;
    }

    public List<DomainTypeUniqueKey> getUniques() {
        return uniques;
    }

    public void setUniques(List<DomainTypeUniqueKey> uniques) {
        this.uniques = uniques;
    }

    public CreateTypeOperation() {
        this.setOperation(StandardMigrationOperationType.CREATE_TYPE.name());
    }

}
