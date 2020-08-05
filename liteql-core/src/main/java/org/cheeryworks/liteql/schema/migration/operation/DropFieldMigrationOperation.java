package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.field.Field;

import java.util.Iterator;
import java.util.List;

public class DropFieldMigrationOperation extends AbstractMigrationOperation {

    private List<String> fields;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public DropFieldMigrationOperation() {
        super(MigrationOperationType.DROP_FIELD);
    }

    @Override
    public void merge(DomainTypeDefinition domainTypeDefinition) {
        for (String field : fields) {
            Iterator<Field> fieldIterator = domainTypeDefinition.getFields().iterator();

            while (fieldIterator.hasNext()) {
                if (fieldIterator.next().getName().equalsIgnoreCase(field)) {
                    fieldIterator.remove();
                }
            }
        }
    }

}
