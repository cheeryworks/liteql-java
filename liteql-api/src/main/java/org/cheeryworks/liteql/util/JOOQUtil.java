package org.cheeryworks.liteql.util;

import org.cheeryworks.liteql.enums.Database;
import org.cheeryworks.liteql.jooq.datatype.JOOQDataType;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.query.condition.ConditionValueConverter;
import org.cheeryworks.liteql.model.query.read.page.Pageable;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.Condition;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.SelectLimitStep;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.DSL;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import static org.cheeryworks.liteql.model.enums.ConditionClause.IN;
import static org.cheeryworks.liteql.model.enums.ConditionClause.NOT_NULL;
import static org.cheeryworks.liteql.model.enums.ConditionClause.NULL;

public class JOOQUtil {

    public static final Map<ConditionType, ConditionValueConverter> CONDITION_VALUE_CONVERTERS;

    static {
        Map<ConditionType, ConditionValueConverter> conditionValueConverters
                = new HashMap<>();
        Iterator<ConditionValueConverter> conditionValueConverterIterator
                = ServiceLoader.load(ConditionValueConverter.class).iterator();

        while (conditionValueConverterIterator.hasNext()) {
            ConditionValueConverter conditionValueConverter = conditionValueConverterIterator.next();

            conditionValueConverters.put(conditionValueConverter.getConditionType(), conditionValueConverter);
        }

        CONDITION_VALUE_CONVERTERS = Collections.unmodifiableMap(conditionValueConverters);
    }

    public static SQLDialect getSqlDialect(String databaseType) {
        Database database = Database.valueOf(databaseType);

        return getSqlDialect(database);
    }

    public static SQLDialect getSqlDialect(Database database) {
        if (database.equals(Database.H2)) {
            return SQLDialect.H2;
        }

        if (database.equals(Database.HSQL)) {
            return SQLDialect.HSQLDB;
        }

        if (database.equals(Database.MYSQL)) {
            return SQLDialect.MYSQL;
        }

        if (database.equals(Database.MARIA_DB)) {
            return SQLDialect.MARIADB;
        }

        if (database.equals(Database.POSTGRESQL)) {
            return SQLDialect.POSTGRES;
        }

        throw new SQLDialectNotSupportedException(database.name());
    }

    public static Database getDatabase(SQLDialect dialect) {
        if (dialect.equals(SQLDialect.H2)) {
            return Database.H2;
        }

        if (dialect.equals(SQLDialect.HSQLDB)) {
            return Database.HSQL;
        }

        if (dialect.equals(SQLDialect.MYSQL)) {
            return Database.MYSQL;
        }

        if (dialect.equals(SQLDialect.MARIADB)) {
            return Database.MARIA_DB;
        }

        if (dialect.equals(SQLDialect.POSTGRES)) {
            return Database.POSTGRESQL;
        }

        return null;
    }

    public static String getPageSql(
            Database database, SelectConditionStep selectConditionStep, Pageable pageable) {
        return getPageSql(database, selectConditionStep, pageable.getPage() * pageable.getSize(), pageable.getSize());
    }

    public static String getPageSql(Database database, SelectLimitStep selectLimitStep, int start, int limit) {
        selectLimitStep.limit(start, limit);

        return selectLimitStep.getSQL();
    }

    public static Condition getCondition(
            TypeName domainTypeName, QueryConditions queryConditions,
            String parentTableAlias, String tableAlias, SqlCustomizer sqlCustomizer) {
        Condition condition = null;

        if (queryConditions != null && queryConditions.size() > 0) {
            for (QueryCondition queryCondition : queryConditions) {
                Condition currentCondition;

                if (queryCondition.getField() != null) {
                    if (!NULL.equals(queryCondition.getCondition())
                            && !NOT_NULL.equals(queryCondition.getCondition())
                            && queryCondition.getValue() == null) {
                        throw new IllegalArgumentException(
                                "Value of condition can not be null, " + queryCondition.toString());
                    }

                    String leftClause = ((tableAlias != null) ? tableAlias + "." : "")
                            + sqlCustomizer.getColumnName(domainTypeName, queryCondition.getField());

                    org.jooq.Field field = (ConditionType.Field.equals(queryCondition.getType()))
                            ? DSL.field(leftClause)
                            : DSL.field(leftClause, JOOQDataType.getDataType(queryCondition.getType()));

                    switch (queryCondition.getCondition()) {
                        case LESS_THAN:
                            currentCondition = field
                                    .lessThan(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                        case LESS_OR_EQUALS:
                            currentCondition = field
                                    .lessOrEqual(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                        case GREATER_THAN:
                            currentCondition = field
                                    .greaterThan(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                        case GREATER_OR_EQUALS:
                            currentCondition = field
                                    .greaterOrEqual(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                        case STARTS_WITH:
                            currentCondition = field
                                    .startsWith(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                        case CONTAINS:
                            currentCondition = field
                                    .contains(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                        case BETWEEN:
                            List<Object> values = (List) getConditionRightClause(
                                    domainTypeName,
                                    queryCondition,
                                    parentTableAlias,
                                    sqlCustomizer);

                            currentCondition = field
                                    .between(values.get(0)).and(values.get(1));
                            break;
                        case IN:
                            currentCondition = field
                                    .in(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                        case NOT_EQUALS:
                            currentCondition = field
                                    .notEqual(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                        case NULL:
                            currentCondition = field.isNull();
                            break;
                        case NOT_NULL:
                            currentCondition = field.isNotNull();
                            break;
                        default:
                            currentCondition = field
                                    .eq(getConditionRightClause(
                                            domainTypeName,
                                            queryCondition,
                                            parentTableAlias,
                                            sqlCustomizer));
                            break;
                    }

                    if (condition != null) {
                        switch (queryCondition.getOperator()) {
                            case OR:
                                condition = condition.or(currentCondition);
                                break;
                            default:
                                condition = condition.and(currentCondition);
                                break;
                        }
                    } else {
                        switch (queryCondition.getOperator()) {
                            case OR:
                                condition = DSL.or(currentCondition);
                                break;
                            default:
                                condition = DSL.and(currentCondition);
                                break;
                        }
                    }
                } else {
                    if (condition != null) {
                        switch (queryCondition.getOperator()) {
                            case OR:
                                condition = condition.or(getCondition(
                                        domainTypeName, queryCondition.getConditions(),
                                        parentTableAlias, tableAlias, sqlCustomizer));
                                break;
                            default:
                                condition = condition.and(getCondition(
                                        domainTypeName, queryCondition.getConditions(),
                                        parentTableAlias, tableAlias, sqlCustomizer));
                                break;
                        }
                    } else {
                        switch (queryCondition.getOperator()) {
                            case OR:
                                condition = DSL.or(getCondition(
                                        domainTypeName, queryCondition.getConditions(),
                                        parentTableAlias, tableAlias, sqlCustomizer));
                                break;
                            default:
                                condition = DSL.and(getCondition(
                                        domainTypeName, queryCondition.getConditions(),
                                        parentTableAlias, tableAlias, sqlCustomizer));
                                break;
                        }
                    }
                }
            }
        }

        return condition;
    }

    private static Object getConditionRightClause(
            TypeName domainTypeName, QueryCondition queryCondition,
            String parentTableAlias, SqlCustomizer sqlCustomizer) {
        if (ConditionType.Field.equals(queryCondition.getType())) {
            return DSL.field(
                    ((parentTableAlias != null) ? parentTableAlias + "." : "")
                            + sqlCustomizer.getColumnName(domainTypeName, queryCondition.getValue().toString()));
        }

        return transformValue(queryCondition);
    }

    private static Object transformValue(QueryCondition queryCondition) {
        if (queryCondition.getValue() instanceof List) {
            List<Object> transformedValues = new LinkedList<Object>();
            for (Object value : (List) queryCondition.getValue()) {
                transformedValues.add(transformValue(queryCondition.getType(), value));
            }

            if (IN.equals(queryCondition.getCondition()) && transformedValues.size() > 500) {
                throw new IllegalArgumentException("Value number of condition clause[IN] more than 500");
            }

            return transformedValues;
        } else {
            return transformValue(queryCondition.getType(), queryCondition.getValue());
        }
    }

    private static Object transformValue(ConditionType conditionType, Object value) {
        try {
            return CONDITION_VALUE_CONVERTERS.get(conditionType).convert(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}
