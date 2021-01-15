package org.cheeryworks.liteql.skeleton.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.skeleton.query.TypedQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryPhase;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.query.event.AbstractListMapQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.AfterCreateQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.AfterDeleteQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.AfterReadQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.AfterUpdateQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.BeforeCreateQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.BeforeDeleteQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.BeforeUpdateQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.QueryEvent;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;

import java.io.IOException;

public class QueryEventDeserializer extends StdDeserializer<QueryEvent> {

    public QueryEventDeserializer() {
        super(AbstractListMapQueryEvent.class);
    }

    @Override
    public QueryEvent deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode queryEventNode = jsonParser.readValueAsTree();

        return getQueryEvent(queryEventNode, jsonParser);
    }

    private QueryEvent getQueryEvent(
            JsonNode queryEventNode, JsonParser jsonParser) throws JsonProcessingException {
        if (queryEventNode.get(TypedQuery.QUERY_TYPE_KEY) != null) {
            JsonNode queryTypeNode = queryEventNode.get(TypedQuery.QUERY_TYPE_KEY);

            if (queryTypeNode != null) {
                QueryType queryType = jsonParser.getCodec().treeToValue(
                        queryEventNode.get(TypedQuery.QUERY_TYPE_KEY), QueryType.class);

                QueryPhase queryPhase = jsonParser.getCodec().treeToValue(
                        queryEventNode.get(QueryEvent.QUERY_PHASE_KEY), QueryPhase.class);

                if (queryEventNode.get(DomainTypeDefinition.DOMAIN_TYPE_NAME_KEY) == null) {
                    throw new IllegalArgumentException("Required field domainType is not specified");
                }

                switch (queryType) {
                    case SingleRead:
                    case TreeRead:
                    case PageRead:
                    case Read:
                        if (QueryPhase.After.equals(queryPhase)) {
                            return jsonParser.getCodec().treeToValue(queryEventNode, AfterReadQueryEvent.class);
                        } else {
                            throw new IllegalArgumentException("Unsupported QueryPhase [" + queryPhase + "]");
                        }
                    case Create:
                        if (QueryPhase.Before.equals(queryPhase)) {
                            return jsonParser.getCodec().treeToValue(queryEventNode, BeforeCreateQueryEvent.class);
                        } else {
                            return jsonParser.getCodec().treeToValue(queryEventNode, AfterCreateQueryEvent.class);
                        }
                    case Update:
                        if (QueryPhase.Before.equals(queryPhase)) {
                            return jsonParser.getCodec().treeToValue(queryEventNode, BeforeUpdateQueryEvent.class);
                        } else {
                            return jsonParser.getCodec().treeToValue(queryEventNode, AfterUpdateQueryEvent.class);
                        }
                    case Delete:
                        if (QueryPhase.Before.equals(queryPhase)) {
                            return jsonParser.getCodec().treeToValue(queryEventNode, BeforeDeleteQueryEvent.class);
                        } else {
                            return jsonParser.getCodec().treeToValue(queryEventNode, AfterDeleteQueryEvent.class);
                        }
                    default:
                        throw new IllegalArgumentException(
                                "Unsupported QueryType [" + queryType + "]");
                }
            } else {
                throw new IllegalArgumentException("QueryType for query not specified");
            }
        }

        throw new IllegalArgumentException(queryEventNode.toString());
    }
}
