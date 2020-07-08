package org.cheeryworks.liteql.util.builder;

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
import org.cheeryworks.liteql.model.util.builder.LiteQLBuilder;
import org.cheeryworks.liteql.model.util.builder.delete.LiteQLDeleteQuery;
import org.cheeryworks.liteql.model.util.builder.save.LiteQLSaveQuery;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.cheeryworks.liteql.model.util.builder.LiteQLBuilderUtil.condition;
import static org.cheeryworks.liteql.model.util.builder.LiteQLBuilderUtil.field;
import static org.cheeryworks.liteql.model.util.builder.LiteQLBuilderUtil.reference;
import static org.cheeryworks.liteql.model.util.builder.LiteQLBuilderUtil.references;
import static org.cheeryworks.liteql.model.util.builder.LiteQLBuilderUtil.saveField;
import static org.cheeryworks.liteql.model.util.builder.read.join.LiteQLReadQueryJoin.join;

public class LiteQLBuilderTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(LiteQLBuilderTest.class);

    @Test
    public void testingSingleReadQueryBuilder() {
        SingleReadQuery singleReadQuery = LiteQLBuilder
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
                        LiteQLBuilder
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
        CreateQuery saveQuery = LiteQLBuilder
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
                        LiteQLSaveQuery.create(new TypeName("liteql_test", "activity"))
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
        List<AbstractSaveQuery> saveQueries = LiteQLBuilder
                .save(
                        LiteQLSaveQuery.create(new TypeName("liteql_test", "project"))
                                .fields(
                                        saveField("code", "A"),
                                        saveField("name", "A"),
                                        saveField("organizationId", "B")
                                )
                                .associations(
                                        references(
                                                reference("id", "projectId")
                                        ),
                                        LiteQLSaveQuery.create(new TypeName("liteql_test", "activity"))
                                                .fields(
                                                        saveField("code", "A"),
                                                        saveField("name", "A")
                                                )
                                                .build()
                                )
                                .build(),
                        LiteQLSaveQuery.update(new TypeName("liteql_test", "project"))
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
        DeleteQuery deleteQuery = LiteQLBuilder
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
        List<DeleteQuery> deleteQueries = LiteQLBuilder
                .delete(
                        LiteQLDeleteQuery.delete(new TypeName("liteql_test", "user"))
                                .conditions(
                                        condition("name", "name")
                                )
                                .build(),
                        LiteQLDeleteQuery.delete(new TypeName("liteql_test", "organization"))
                                .conditions(
                                        condition("code", "code")
                                )
                                .build()
                )
                .getQueries();

        logger.info(LiteQLJsonUtil.toJson(getObjectMapper(), deleteQueries));
    }

}
