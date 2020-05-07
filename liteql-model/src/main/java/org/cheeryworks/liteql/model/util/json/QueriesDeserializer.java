package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.AbstractQuery;
import org.cheeryworks.liteql.model.query.CreateQuery;
import org.cheeryworks.liteql.model.query.DeleteQuery;
import org.cheeryworks.liteql.model.query.PageReadQuery;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.ReadQuery;
import org.cheeryworks.liteql.model.query.SaveQuery;
import org.cheeryworks.liteql.model.query.SingleReadQuery;
import org.cheeryworks.liteql.model.query.TreeReadQuery;
import org.cheeryworks.liteql.model.query.UpdateQuery;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class QueriesDeserializer extends StdDeserializer<Queries> {

    public QueriesDeserializer() {
        this(null);
    }

    public QueriesDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Queries deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        Queries queries = new Queries();

        JsonNode typedQueriesInJson = jsonParser.getCodec().readTree(jsonParser);

        Iterator<String> fieldNamesIterator = typedQueriesInJson.fieldNames();

        while (fieldNamesIterator.hasNext()) {
            String fieldName = fieldNamesIterator.next();

            JsonNode typedQueriesInGroupInJson = typedQueriesInJson.get(fieldName);

            if (typedQueriesInGroupInJson instanceof ArrayNode) {
                List<AbstractQuery> typedQueriesInGroup = new LinkedList<>();

                for (JsonNode typedQueryInGroupInJson : typedQueriesInGroupInJson) {
                    QueryType queryType = QueryType.valueOf(
                            StringUtils.capitalize(
                                    typedQueryInGroupInJson.get(AbstractQuery.QUERY_TYPE_KEY).asText()));

                    Class queryClass;

                    switch (queryType) {
                        case Create:
                            queryClass = CreateQuery.class;
                            break;
                        case Update:
                            queryClass = UpdateQuery.class;
                            break;
                        case Save:
                            queryClass = SaveQuery.class;
                            break;
                        case Delete:
                            queryClass = DeleteQuery.class;
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported query type: " + queryType);
                    }

                    typedQueriesInGroup.add(
                            (AbstractQuery) LiteQLJsonUtil.OBJECT_MAPPER
                                    .treeToValue(typedQueryInGroupInJson, queryClass));
                }

                queries.put(fieldName, typedQueriesInGroup);
            } else {
                QueryType queryType = QueryType.valueOf(
                        StringUtils.capitalize(typedQueriesInGroupInJson.get(AbstractQuery.QUERY_TYPE_KEY).asText()));

                Class queryClass;

                switch (queryType) {
                    case Read:
                        queryClass = ReadQuery.class;
                        break;
                    case SingleRead:
                        queryClass = SingleReadQuery.class;
                        break;
                    case TreeRead:
                        queryClass = TreeReadQuery.class;
                        break;
                    case PageRead:
                        queryClass = PageReadQuery.class;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported query type: " + queryType);
                }

                queries.put(
                        fieldName, LiteQLJsonUtil.OBJECT_MAPPER.treeToValue(typedQueriesInGroupInJson, queryClass));
            }
        }

        return queries;
    }

}
