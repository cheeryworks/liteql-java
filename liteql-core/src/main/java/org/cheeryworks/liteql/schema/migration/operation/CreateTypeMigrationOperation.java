package org.cheeryworks.liteql.schema.migration.operation;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.index.Index;
import org.cheeryworks.liteql.schema.index.Unique;

import java.util.Set;

public class CreateTypeMigrationOperation extends AbstractMigrationOperation {

    private Set<Field> fields;

    private Set<Unique> uniques;

    private Set<Index> indexes;

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public Set<Unique> getUniques() {
        return uniques;
    }

    public void setUniques(Set<Unique> uniques) {
        this.uniques = uniques;
    }

    public Set<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(Set<Index> indexes) {
        this.indexes = indexes;
    }

    public CreateTypeMigrationOperation() {
        super(MigrationOperationType.CREATE_TYPE);
    }

    @Override
    public void merge(DomainType domainType) {
        CreateFieldMigrationOperation createFieldMigrationOperation
                = new CreateFieldMigrationOperation(fields);
        createFieldMigrationOperation.merge(domainType);

        if (CollectionUtils.isNotEmpty(uniques)) {
            CreateUniqueMigrationOperation createUniqueMigrationOperation
                    = new CreateUniqueMigrationOperation(uniques);
            createUniqueMigrationOperation.merge(domainType);
        }

        if (CollectionUtils.isNotEmpty(indexes)) {
            CreateIndexMigrationOperation createIndexMigrationOperation
                    = new CreateIndexMigrationOperation(indexes);
            createIndexMigrationOperation.merge(domainType);
        }
    }

}
