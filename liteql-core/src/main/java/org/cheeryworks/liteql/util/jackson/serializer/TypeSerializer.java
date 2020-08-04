package org.cheeryworks.liteql.util.jackson.serializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.cheeryworks.liteql.schema.Type;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class TypeSerializer extends StdSerializer<Type> {

    public TypeSerializer() {
        super(Type.class);
    }

    @Override
    public void serialize(Type value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        if (value.isTrait()) {
            gen.writeObjectField("trait", true);
        }

        List<Field> fields = FieldUtils.getAllFieldsList(value.getClass());

        for (Field field : fields) {
            JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);

            if (jsonIgnore != null && jsonIgnore.value()) {
                continue;
            }

            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                Object fieldValue = FieldUtils.readField(field, value, true);

                if (fieldValue != null) {
                    gen.writeObjectField(field.getName(), fieldValue);

                }
            } catch (Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
        }

        gen.writeEndObject();
    }

}
