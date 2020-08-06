package org.cheeryworks.liteql.schema.migration.operation;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.index.IndexDefinition;
import org.cheeryworks.liteql.schema.index.UniqueDefinition;

import java.util.Set;

public class CreateTypeMigrationOperation extends AbstractMigrationOperation {

    private Set<Field> fields;

    private Set<UniqueDefinition> uniques;

    private Set<IndexDefinition> indexes;

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public Set<UniqueDefinition> getUniques() {
        return uniques;
    }

    public void setUniques(Set<UniqueDefinition> uniqueDefinitions) {
        this.uniques = uniqueDefinitions;
    }

    public Set<IndexDefinition> getIndexes() {
        return indexes;
    }

    public void setIndexes(Set<IndexDefinition> indexes) {
        this.indexes = indexes;
    }

    public CreateTypeMigrationOperation() {
        super(MigrationOperationType.CREATE_TYPE);
    }

    @Override
    public void merge(DomainTypeDefinition domainTypeDefinition) {
        CreateFieldMigrationOperation createFieldMigrationOperation
                = new CreateFieldMigrationOperation(fields);
        createFieldMigrationOperation.merge(domainTypeDefinition);

        if (CollectionUtils.isNotEmpty(uniques)) {
            CreateUniqueMigrationOperation createUniqueMigrationOperation
                    = new CreateUniqueMigrationOperation(uniques);
            createUniqueMigrationOperation.merge(domainTypeDefinition);
        }

        if (CollectionUtils.isNotEmpty(indexes)) {
            CreateIndexMigrationOperation createIndexMigrationOperation
                    = new CreateIndexMigrationOperation(indexes);
            createIndexMigrationOperation.merge(domainTypeDefinition);
        }
    }

}