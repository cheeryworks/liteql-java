package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.migration.operation.CreateFieldMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateTypeMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropFieldMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropTypeMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropUniqueMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.MigrationOperation;

import java.io.IOException;

public class MigrationOperationDeserializer extends StdDeserializer<MigrationOperation> {

    public MigrationOperationDeserializer() {
        super(MigrationOperation.class);
    }

    @Override
    public MigrationOperation deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String migrationOperationTypeInString = node.get("type").textValue();

        if (StringUtils.isNotBlank(migrationOperationTypeInString)) {
            try {
                MigrationOperationType migrationOperationType = MigrationOperationType.valueOf(
                        StringUtils.upperCase(migrationOperationTypeInString));

                switch (migrationOperationType) {
                    case CREATE_TYPE:
                        return jsonParser.getCodec().treeToValue(node, CreateTypeMigrationOperation.class);
                    case DROP_TYPE:
                        return jsonParser.getCodec().treeToValue(node, DropTypeMigrationOperation.class);
                    case CREATE_FIELD:
                        return jsonParser.getCodec().treeToValue(node, CreateFieldMigrationOperation.class);
                    case DROP_FIELD:
                        return jsonParser.getCodec().treeToValue(node, DropFieldMigrationOperation.class);
                    case CREATE_UNIQUE:
                        return jsonParser.getCodec().treeToValue(node, CreateUniqueMigrationOperation.class);
                    case DROP_UNIQUE:
                        return jsonParser.getCodec().treeToValue(node, DropUniqueMigrationOperation.class);
                    default:
                        throw new IllegalArgumentException(
                                "Unsupported migration operation: " + migrationOperationTypeInString);
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        } else {
            throw new IllegalArgumentException("Migration operation name not specified");
        }
    }

}
