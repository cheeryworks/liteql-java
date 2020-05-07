package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.enums.QueryType;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class QueryTypeDeserializer extends StdDeserializer<QueryType> {

    public QueryTypeDeserializer() {
        super(QueryType.class);
    }

    @Override
    public QueryType deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String queryTypeInString = node.asText();

        if (StringUtils.isNotBlank(queryTypeInString)) {
            try {
                QueryType queryType = QueryType.valueOf(
                        StringUtils.capitalize(
                                queryTypeInString));

                return queryType;
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                        "Unsupported query type: " + queryTypeInString);
            }
        } else {
            throw new IllegalArgumentException("Query type not specified");
        }
    }

}
