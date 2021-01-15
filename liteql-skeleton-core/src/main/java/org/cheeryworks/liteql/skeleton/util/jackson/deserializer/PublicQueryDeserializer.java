package org.cheeryworks.liteql.skeleton.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.Queries;
import org.cheeryworks.liteql.skeleton.query.TypedQuery;
import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.query.read.PageReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.ReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.SingleReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.TreeReadQuery;
import org.cheeryworks.liteql.skeleton.query.save.CreateQuery;
import org.cheeryworks.liteql.skeleton.query.save.SaveQueries;
import org.cheeryworks.liteql.skeleton.query.save.UpdateQuery;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;

import java.io.IOException;
import java.util.Iterator;

public class PublicQueryDeserializer extends StdDeserializer<PublicQuery> {

    public PublicQueryDeserializer() {
        super(PublicQuery.class);
    }

    @Override
    public PublicQuery deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode query = jsonParser.readValueAsTree();

        return getQuery(query, jsonParser);
    }

    private PublicQuery getQuery(JsonNode query, JsonParser jsonParser) throws JsonProcessingException {
        if (query instanceof ObjectNode) {
            if (query.get(TypedQuery.QUERY_TYPE_KEY) != null) {
                JsonNode queryTypeNode = query.get(TypedQuery.QUERY_TYPE_KEY);

                if (queryTypeNode != null) {
                    QueryType queryType = jsonParser.getCodec().treeToValue(
                            query.get(TypedQuery.QUERY_TYPE_KEY), QueryType.class);

                    if (query.get(DomainTypeDefinition.DOMAIN_TYPE_NAME_KEY) == null) {
                        throw new IllegalArgumentException("Required field domainType is not specified");
                    }

                    switch (queryType) {
                        case SingleRead:
                            return jsonParser.getCodec().treeToValue(query, SingleReadQuery.class);
                        case TreeRead:
                            return jsonParser.getCodec().treeToValue(query, TreeReadQuery.class);
                        case PageRead:
                            return jsonParser.getCodec().treeToValue(query, PageReadQuery.class);
                        case Read:
                            return jsonParser.getCodec().treeToValue(query, ReadQuery.class);
                        case Create:
                            return jsonParser.getCodec().treeToValue(query, CreateQuery.class);
                        case Update:
                            return jsonParser.getCodec().treeToValue(query, UpdateQuery.class);
                        case Delete:
                            return jsonParser.getCodec().treeToValue(query, DeleteQuery.class);
                        default:
                            throw new IllegalArgumentException(
                                    "Unsupported QueryType [" + queryTypeNode.asText() + "]");
                    }
                } else {
                    throw new IllegalArgumentException("QueryType for query not specified");
                }
            } else {
                Queries queries = new Queries();

                Iterator<String> fieldNamesIterator = query.fieldNames();

                while (fieldNamesIterator.hasNext()) {
                    String fieldName = fieldNamesIterator.next();

                    queries.put(fieldName, getQuery(query.get(fieldName), jsonParser));
                }

                return queries;
            }
        } else if (query instanceof ArrayNode) {
            ArrayNode queryInArray = (ArrayNode) query;

            SaveQueries saveQueries = new SaveQueries();

            Iterator<JsonNode> saveQueryNodeIterator = queryInArray.iterator();

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

        throw new IllegalArgumentException(query.toString());
    }

}
