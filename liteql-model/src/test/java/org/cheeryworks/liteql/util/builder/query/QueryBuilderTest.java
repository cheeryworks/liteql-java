package org.cheeryworks.liteql.util.builder.query;

import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.model.enums.ConditionClause;
import org.cheeryworks.liteql.model.enums.ConditionOperator;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.SingleReadQuery;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.model.util.builder.query.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.cheeryworks.liteql.model.util.builder.query.QueryBuilderUtil.condition;
import static org.cheeryworks.liteql.model.util.builder.query.QueryBuilderUtil.field;
import static org.cheeryworks.liteql.model.util.builder.query.QueryBuilderUtil.reference;
import static org.cheeryworks.liteql.model.util.builder.query.QueryBuilderUtil.references;
import static org.cheeryworks.liteql.model.util.builder.query.QueryBuilderUtil.saveField;
import static org.cheeryworks.liteql.model.util.builder.query.read.join.ReadQueryJoinMetadata.join;

public class QueryBuilderTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(QueryBuilderTest.class);

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

        logger.info(LiteQLJsonUtil.toJson(getObjectMapper(), singleReadQuery));
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

        logger.info(LiteQLJsonUtil.toJson(getObjectMapper(), saveQuery));
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

        logger.info(LiteQLJsonUtil.toJson(getObjectMapper(), saveQueries));
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

        logger.info(LiteQLJsonUtil.toJson(getObjectMapper(), deleteQuery));
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

        logger.info(LiteQLJsonUtil.toJson(getObjectMapper(), deleteQueries));
    }

}
