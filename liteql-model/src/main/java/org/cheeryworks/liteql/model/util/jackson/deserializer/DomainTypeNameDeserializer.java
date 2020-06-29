package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.type.DomainTypeName;

import java.io.IOException;

public class DomainTypeNameDeserializer extends StdDeserializer<DomainTypeName> {

    public DomainTypeNameDeserializer() {
        super(DomainTypeName.class);
    }

    @Override
    public DomainTypeName deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String domainTypeNameInString = jsonParser.getValueAsString();

        if (StringUtils.isNotBlank(domainTypeNameInString)) {
            String[] domainTypeNameParts = domainTypeNameInString.split("\\.");

            DomainTypeName domainTypeName = new DomainTypeName();
            domainTypeName.setSchema(domainTypeNameParts[0]);
            domainTypeName.setName(domainTypeNameParts[1]);

            return domainTypeName;
        } else {
            throw new IllegalArgumentException("Domain Type Name not specified");
        }
    }

}
