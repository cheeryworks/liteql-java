package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.TypedQuery;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.PageReadQuery;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.query.read.SingleReadQuery;
import org.cheeryworks.liteql.model.query.read.TreeReadQuery;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.SaveQueries;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.TypeName;

import java.io.IOException;
import java.util.Iterator;

public class PublicQueryDeserializer extends StdDeserializer<PublicQuery> {

    public PublicQueryDeserializer() {
        super(PublicQuery.class);
    }

    @Override
    public PublicQuery deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode cql = jsonParser.readValueAsTree();

        return getQuery(cql, jsonParser);
    }

    private PublicQuery getQuery(JsonNode cql, JsonParser jsonParser) throws JsonProcessingException {
        if (cql instanceof ObjectNode) {
            if (cql.get(TypedQuery.QUERY_TYPE_KEY) != null) {
                JsonNode queryTypeNode = cql.get(TypedQuery.QUERY_TYPE_KEY);

                if (queryTypeNode != null) {
                    QueryType queryType = jsonParser.getCodec().treeToValue(
                            cql.get(TypedQuery.QUERY_TYPE_KEY), QueryType.class);

                    if (cql.get(TypeName.DOMAIN_TYPE_NAME_KEY) == null) {
                        throw new IllegalArgumentException("Required field domainType is not specified");
                    }

                    switch (queryType) {
                        case SingleRead:
                            return jsonParser.getCodec().treeToValue(cql, SingleReadQuery.class);
                        case TreeRead:
                            return jsonParser.getCodec().treeToValue(cql, TreeReadQuery.class);
                        case PageRead:
                            return jsonParser.getCodec().treeToValue(cql, PageReadQuery.class);
                        case Read:
                            return jsonParser.getCodec().treeToValue(cql, ReadQuery.class);
                        case Create:
                            return jsonParser.getCodec().treeToValue(cql, CreateQuery.class);
                        case Update:
                            return jsonParser.getCodec().treeToValue(cql, UpdateQuery.class);
                        case Delete:
                            return jsonParser.getCodec().treeToValue(cql, DeleteQuery.class);
                        default:
                            throw new IllegalArgumentException(
                                    "Unsupported QueryType [" + queryTypeNode.asText() + "]");
                    }
                } else {
                    throw new IllegalArgumentException("QueryType for query not specified");
                }
            } else {
                Queries queries = new Queries();

                Iterator<String> fieldNamesIterator = cql.fieldNames();

                while (fieldNamesIterator.hasNext()) {
                    String fieldName = fieldNamesIterator.next();

                    queries.put(fieldName, getQuery(cql.get(fieldName), jsonParser));
                }

                return queries;
            }
        } else if (cql instanceof ArrayNode) {
            ArrayNode cqlInArray = (ArrayNode) cql;

            SaveQueries saveQueries = new SaveQueries();

            Iterator<JsonNode> saveQueryNodeIterator = cqlInArray.iterator();

            while (saveQueryNodeIterator.hasNext()) {
                JsonNode saveQueryNode = saveQueryNodeIterator.next();

                JsonNode queryTypeNode = saveQueryNode.get(TypedQuery.QUERY_TYPE_KEY);

                if (queryTypeNode != null) {
                    QueryType queryType = jsonParser.getCodec().treeToValue(
                            saveQueryNode.get(TypedQuery.QUERY_TYPE_KEY), QueryType.class);

                    if (QueryType.Create.equals(queryType)) {
                        saveQueries.add(jsonParser.getCodec().treeToValue(saveQueryNode, CreateQuery.class));
                    } else if (QueryType.Update.equals((queryType))) {
                        saveQueries.add(jsonParser.getCodec().treeToValue(saveQueryNode, UpdateQuery.class));
                    } else {
                        throw new IllegalArgumentException("Unsupported QueryType [" + queryTypeNode.asText() + "]");
                    }
                } else {
                    throw new IllegalArgumentException("QueryType for query not specified");
                }
            }

            return saveQueries;
        }

        throw new UnsupportedOperationException(cql.toString());
    }

}
