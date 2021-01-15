package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.index.IndexDefinition;
import org.cheeryworks.liteql.skeleton.schema.index.UniqueDefinition;

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

}
