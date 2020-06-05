package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.index.Index;
import org.cheeryworks.liteql.model.type.index.Unique;

import java.util.List;

public class CreateTypeMigrationOperation extends AbstractMigrationOperation {

    private List<Field> fields;

    private List<Unique> uniques;

    private List<Index> indexes;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Unique> getUniques() {
        return uniques;
    }

    public void setUniques(List<Unique> uniques) {
        this.uniques = uniques;
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    public CreateTypeMigrationOperation() {
        super(MigrationOperationType.CREATE_TYPE);
    }

}
