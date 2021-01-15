package org.cheeryworks.liteql.skeleton.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;

import java.io.IOException;

public class MigrationOperationTypeSerializer extends StdSerializer<MigrationOperationType> {

    public MigrationOperationTypeSerializer() {
        super(MigrationOperationType.class);
    }

    @Override
    public void serialize(
            MigrationOperationType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
