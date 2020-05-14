package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.type.migration.MigrationOperation;
import org.cheeryworks.liteql.model.type.DomainTypeField;

import java.util.List;

public class CreateFieldOperation extends MigrationOperation {

    private List<DomainTypeField> fields;

    public List<DomainTypeField> getFields() {
        return fields;
    }

    public void setFields(List<DomainTypeField> fields) {
        this.fields = fields;
    }

    public CreateFieldOperation() {
        this.setOperation(StandardMigrationOperationType.CREATE_FIELD.name());
    }

}
