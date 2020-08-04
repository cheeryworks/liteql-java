package org.cheeryworks.liteql.schema.migration.operation;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.field.Field;

import java.util.HashSet;
import java.util.Set;

public class CreateFieldMigrationOperation extends AbstractMigrationOperation {

    private Set<Field> fields;

    public Set<Field> getFields() {
        return fields;
    }

    public CreateFieldMigrationOperation() {
        this(null);
    }

    public CreateFieldMigrationOperation(Set<Field> fields) {
        super(MigrationOperationType.CREATE_FIELD);

        this.fields = fields;
    }

    @Override
    public void merge(DomainType domainType) {
        if (CollectionUtils.isEmpty(domainType.getFields())) {
            domainType.setFields(new HashSet<>());
        }

        domainType.getFields().addAll(fields);
    }

}
