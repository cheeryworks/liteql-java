package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.model.enums.MigrationOperationType;

import java.io.IOException;

public class MigrationOperationTypeSerializer extends StdSerializer<MigrationOperationType> {

    protected MigrationOperationTypeSerializer() {
        super(MigrationOperationType.class);
    }

    @Override
    public void serialize(
            MigrationOperationType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
