package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.QueryType;

import java.io.IOException;

public class QueryTypeDeserializer extends StdDeserializer<QueryType> {

    public QueryTypeDeserializer() {
        super(QueryType.class);
    }

    @Override
    public QueryType deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String queryTypeInString = jsonParser.getValueAsString();

        if (StringUtils.isNotBlank(queryTypeInString)) {
            for (QueryType queryType : QueryType.values()) {
                if (queryType.name().toLowerCase().equals(queryTypeInString.toLowerCase().replaceAll("_", ""))) {
                    return queryType;
                }
            }

            throw new IllegalArgumentException(
                    "Unsupported query type: " + queryTypeInString);

        } else {
            throw new IllegalArgumentException("Query type not specified");
        }
    }

}
