package org.cheeryworks.liteql.model.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.DomainTypeName;

import java.io.IOException;

public class DomainTypeNameSerializer extends StdSerializer<DomainTypeName> {

    public DomainTypeNameSerializer() {
        super(DomainTypeName.class);
    }

    @Override
    public void serialize(DomainTypeName value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value instanceof DomainType) {
            DomainType domainType = (DomainType) value;
            gen.writeStartObject();

            if (domainType.getFields() != null) {
                gen.writeObjectField("fields", domainType.getFields());
            }

            if (domainType.getUniques() != null) {
                gen.writeObjectField("uniques", domainType.getUniques());
            }

            if (domainType.getIndexes() != null) {
                gen.writeObjectField("indexes", domainType.getIndexes());
            }

            gen.writeEndObject();
        } else {
            gen.writeString(value.getFullname());
        }
    }

}
