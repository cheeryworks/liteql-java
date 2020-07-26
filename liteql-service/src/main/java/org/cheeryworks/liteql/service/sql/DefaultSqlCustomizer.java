package org.cheeryworks.liteql.service.sql;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;
import org.cheeryworks.liteql.schema.field.ReferenceField;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.util.LiteQLConstants;

public class DefaultSqlCustomizer implements SqlCustomizer {

    private SchemaService schemaService;

    public DefaultSqlCustomizer(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @Override
    public String getColumnName(TypeName domainTypeName, String fieldName) {
        Field field = this.schemaService
                .getDomainType(domainTypeName)
                .getFields()
                .stream()
                .filter(currentField -> currentField.getName().equalsIgnoreCase(fieldName))
                .findFirst()
                .<IllegalArgumentException>orElseThrow(() -> {
                    throw new IllegalArgumentException("Can not get field [" + fieldName + "]");
                });

        String columnName = field.getName().replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                "_"
        ).toLowerCase();

        if (field instanceof ReferenceField) {
            return columnName + LiteQLConstants.WORD_CONCAT + IdField.ID_FIELD_NAME;
        } else {
            return columnName;
        }
    }

}
