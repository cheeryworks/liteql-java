package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.enums.StandardMigrationOperationType;
import org.cheeryworks.liteql.model.type.migration.MigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateFieldOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateTypeOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateUniqueOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DeleteFieldOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DeleteTypeOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DeleteUniqueOperation;
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

        String migrationOperationTypeInString = node.get("operation").textValue();

        if (StringUtils.isNotBlank(migrationOperationTypeInString)) {
            try {
                StandardMigrationOperationType migrationOperationType = StandardMigrationOperationType.valueOf(
                        StringUtils.upperCase(migrationOperationTypeInString));

                switch (migrationOperationType) {
                    case CREATE_TYPE:
                        return LiteQLJsonUtil.toBean(node.toString(), CreateTypeOperation.class);
                    case DELETE_TYPE:
                        return LiteQLJsonUtil.toBean(node.toString(), DeleteTypeOperation.class);
                    case CREATE_FIELD:
                        return LiteQLJsonUtil.toBean(node.toString(), CreateFieldOperation.class);
                    case DELETE_FIELD:
                        return LiteQLJsonUtil.toBean(node.toString(), DeleteFieldOperation.class);
                    case CREATE_UNIQUE:
                        return LiteQLJsonUtil.toBean(node.toString(), CreateUniqueOperation.class);
                    case DELETE_UNIQUE:
                        return LiteQLJsonUtil.toBean(node.toString(), DeleteUniqueOperation.class);
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
