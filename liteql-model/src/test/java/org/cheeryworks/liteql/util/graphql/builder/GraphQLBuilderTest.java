package org.cheeryworks.liteql.util.graphql.builder;

import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.graphql.GraphQLQuery;
import org.cheeryworks.liteql.query.QueryCondition;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static org.cheeryworks.liteql.util.graphql.builder.GraphQLEntryFieldQueryBuilder.field;

public class GraphQLBuilderTest {

    private Logger logger = LoggerFactory.getLogger(GraphQLBuilderTest.class);

    @Test
    public void testingGraphQLBuilder() {
        List<QueryCondition> conditions = new LinkedList<>();
        conditions.add(
                new QueryCondition(
                        "code", ConditionClause.EQUALS, ConditionType.String,
                        "MessageType"));

        GraphQLQuery query = GraphQLBuilder
                .query()
                .variable("conditions", QueryCondition.asMap(conditions), "[Condition!]")
                .fields(
                        field("dictionaries")
                                .argument("conditions", "conditions")
                                .childField("names")
                                .childField("dictionaryOptions", GraphQLChildFieldQueryBuilder.newBuilder()
                                        .childField("id")
                                        .childField("names")
                                )
                                .build(),
                        field("domains")
                                .childField("name")
                                .childField("user_domains", GraphQLChildFieldQueryBuilder.newBuilder()
                                        .childField("user", GraphQLChildFieldQueryBuilder.newBuilder()
                                                .childField("name")
                                        )
                                        .childField("domain", GraphQLChildFieldQueryBuilder.newBuilder()
                                                .childField("name")
                                        ))
                                .build()
                )
                .build();

        logger.info(query.toString());
    }

}
