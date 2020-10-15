package org.cheeryworks.liteql.service.sql;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;
import org.cheeryworks.liteql.schema.field.ReferenceField;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.util.LiteQL;

public class DefaultSqlCustomizer implements SqlCustomizer {

    private SchemaService schemaService;

    public DefaultSqlCustomizer(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @Override
    public String getColumnName(TypeName domainTypeName, String fieldName) {
        Field field = this.schemaService
                .getDomainTypeDefinition(domainTypeName)
                .getFields()
                .stream()
                .filter(currentField -> currentField.getName().equalsIgnoreCase(fieldName))
                .findFirst()
                .orElse(null);

        String columnName = fieldName;

        if (field != null) {
            columnName = field.getName().replaceAll(
                    String.format("%s|%s|%s",
                            "(?<=[A-Z])(?=[A-Z][a-z])",
                            "(?<=[^A-Z])(?=[A-Z])",
                            "(?<=[A-Za-z])(?=[^A-Za-z])"
                    ),
                    "_"
            ).toLowerCase();
        }

        if (field instanceof ReferenceField) {
            return columnName + LiteQL.Constants.WORD_CONCAT + IdField.ID_FIELD_NAME;
        } else {
            return columnName;
        }
    }

}
