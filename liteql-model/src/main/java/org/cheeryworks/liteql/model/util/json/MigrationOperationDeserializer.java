package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateFieldMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateTypeMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropFieldMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropTypeMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropUniqueMigrationOperation;
import org.apache.commons.lang3.StringUtils;

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
                        return LiteQLJsonUtil.toBean(node.toString(), CreateTypeMigrationOperation.class);
                    case DROP_TYPE:
                        return LiteQLJsonUtil.toBean(node.toString(), DropTypeMigrationOperation.class);
                    case CREATE_FIELD:
                        return LiteQLJsonUtil.toBean(node.toString(), CreateFieldMigrationOperation.class);
                    case DROP_FIELD:
                        return LiteQLJsonUtil.toBean(node.toString(), DropFieldMigrationOperation.class);
                    case CREATE_UNIQUE:
                        return LiteQLJsonUtil.toBean(node.toString(), CreateUniqueMigrationOperation.class);
                    case DROP_UNIQUE:
                        return LiteQLJsonUtil.toBean(node.toString(), DropUniqueMigrationOperation.class);
                    default:
                        throw new IllegalArgumentException(
                                "Unsupported migration operation: " + migrationOperationTypeInString);
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                        "Unsupported migration operation: " + migrationOperationTypeInString);
            }
        } else {
            throw new IllegalArgumentException("Migration operation name not specified");
        }
    }

}
