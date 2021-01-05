package org.cheeryworks.liteql.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.query.enums.QueryPhase;

import java.io.IOException;

public class QueryPhaseDeserializer extends StdDeserializer<QueryPhase> {

    public QueryPhaseDeserializer() {
        super(QueryPhase.class);
    }

    @Override
    public QueryPhase deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String queryPhaseInString = jsonParser.getValueAsString();

        if (StringUtils.isNotBlank(queryPhaseInString)) {
            for (QueryPhase queryPhase : QueryPhase.values()) {
                if (queryPhase.name().toLowerCase().equals(queryPhaseInString.toLowerCase().replaceAll("_", ""))) {
                    return queryPhase;
                }
            }

            throw new IllegalArgumentException(
                    "Unsupported query phase: " + queryPhaseInString);

        } else {
            throw new IllegalArgumentException("Query phase not specified");
        }
    }

}
