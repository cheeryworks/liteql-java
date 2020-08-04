package org.cheeryworks.liteql.util.query.builder;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.query.delete.DeleteQuery;
import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionOperator;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.query.read.ReadQuery;
import org.cheeryworks.liteql.query.read.SingleReadQuery;
import org.cheeryworks.liteql.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.query.save.CreateQuery;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.util.LiteQL;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.cheeryworks.liteql.util.query.builder.QueryBuilderUtil.condition;
import static org.cheeryworks.liteql.util.query.builder.QueryBuilderUtil.field;
import static org.cheeryworks.liteql.util.query.builder.QueryBuilderUtil.reference;
import static org.cheeryworks.liteql.util.query.builder.QueryBuilderUtil.references;
import static org.cheeryworks.liteql.util.query.builder.QueryBuilderUtil.saveField;
import static org.cheeryworks.liteql.util.query.builder.read.join.ReadQueryJoinMetadata.join;

public class QueryBuilderTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(QueryBuilderTest.class);

    @Test
    public void testingReadQueryBuilder() {
        ReadQuery readQuery = QueryBuilder
                .read(new TypeName("liteql_test", "user"))
                .getQuery();

        logger.info(LiteQL.JacksonJsonUtils.toJson(readQuery));
    }

    @Test
    public void testingSingleReadQueryBuilder() {
        SingleReadQuery singleReadQuery = QueryBuilder
                .read(new TypeName("liteql_test", "user"))
                .fields(
                        field("id"),
                        field("name"),
                        field("username", "account")
                )
                .joins(
                        join(new TypeName("liteql_test", "user_organization"))
                                .fields(
                                        field("organizationId")
                                )
                                .conditions(
                                        condition("userId", "id")
                                )
                                .build(),
                        join(new TypeName("liteql_test", "organization"))
                                .fields(
                                        field("code", "organizationCode"),
                                        field("name", "organizationName")
                                )
                                .conditions(
                                        condition("id", "organizationId")
                                )
                                .build()
                )
                .conditions(
                        condition("username", "zz"),
                        condition("name", ConditionClause.CONTAINS, ConditionType.String, "Z")
                                .conditions(
                                        condition(
                                                ConditionOperator.OR,
                                                "name",
                                                ConditionClause.CONTAINS,
                                                ConditionType.String, "K"
                                        )
                                )
                )
                .single()
                .associations(
                        references(
                                reference("id", "userId")
                        ),
                        QueryBuilder
                                .read(new TypeName("liteql_test", "user"))
                                .fields(
                                        field("id"),
                                        field("name", "userName")
                                )
                                .joins(
                                        join(new TypeName("liteql_test", "user_organization"))
                                                .fields(
                                                        field("organizationId")
                                                )
                                                .conditions(
                                                        condition("userId", "id")
                                                )
                                                .build(),
                                        join(new TypeName("liteql_test", "organization"))
                                                .fields(
                                                        field("code", "organizationCode"),
                                                        field("name", "organizationName")
                                                )
                                                .conditions(
                                                        condition("id", "organizationId")
                                                )
                                                .build()
                                )
                                .conditions(
                                        condition("name", "zz")
                                )
                                .single()
                                .getQuery()
                )
                .getQuery();

        logger.info(LiteQL.JacksonJsonUtils.toJson(singleReadQuery));
    }

    @Test
    public void testingSingleSaveQueryBuilder() {
        CreateQuery saveQuery = QueryBuilder
                .create(new TypeName("liteql_test", "project"))
                .fields(
                        saveField("code", "A"),
                        saveField("name", "A"),
                        saveField("organizationId", "B")
                )
                .associations(
                        references(
                                reference("id", "projectId")
                        ),
                        QueryBuilder.create(new TypeName("liteql_test", "activity"))
                                .fields(
                                        saveField("code", "A"),
                                        saveField("name", "A")
                                )
                                .build()
                )
                .getQuery();

        logger.info(LiteQL.JacksonJsonUtils.toJson(saveQuery));
    }

    @Test
    public void testingSaveQueryBuilder() {
        List<AbstractSaveQuery> saveQueries = QueryBuilder
                .save(
                        QueryBuilder.create(new TypeName("liteql_test", "project"))
                                .fields(
                                        saveField("code", "A"),
                                        saveField("name", "A"),
                                        saveField("organizationId", "B")
                                )
                                .associations(
                                        references(
                                                reference("id", "projectId")
                                        ),
                                        QueryBuilder.create(new TypeName("liteql_test", "activity"))
                                                .fields(
                                                        saveField("code", "A"),
                                                        saveField("name", "A")
                                                )
                                                .build()
                                )
                                .build(),
                        QueryBuilder.update(new TypeName("liteql_test", "project"))
                                .fields(
                                        saveField("code", "B"),
                                        saveField("name", "B"),
                                        saveField("organizationId", "B")
                                )
                                .build()
                )
                .getQueries();

        logger.info(LiteQL.JacksonJsonUtils.toJson(saveQueries));
    }

    @Test
    public void testingSingleDeleteQueryBuilder() {
        DeleteQuery deleteQuery = QueryBuilder
                .delete(new TypeName("liteql_test", "user"))
                .conditions(
                        condition("name", "name")
                )
                .truncated()
                .getQuery();

        logger.info(LiteQL.JacksonJsonUtils.toJson(deleteQuery));
    }

    @Test
    public void testingDeleteQueryBuilder() {
        List<DeleteQuery> deleteQueries = QueryBuilder
                .delete(
                        QueryBuilder.delete(new TypeName("liteql_test", "user"))
                                .conditions(
                                        condition("name", "name")
                                )
                                .build(),
                        QueryBuilder.delete(new TypeName("liteql_test", "organization"))
                                .conditions(
                                        condition("code", "code")
                                )
                                .build()
                )
                .getQueries();

        logger.info(LiteQL.JacksonJsonUtils.toJson(deleteQueries));
    }

}
