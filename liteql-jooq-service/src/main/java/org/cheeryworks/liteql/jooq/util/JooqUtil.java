package org.cheeryworks.liteql.jooq.util;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.skeleton.enums.Database;
import org.cheeryworks.liteql.skeleton.query.QueryCondition;
import org.cheeryworks.liteql.skeleton.query.QueryConditions;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionType;
import org.cheeryworks.liteql.skeleton.query.read.page.Pageable;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.field.DecimalField;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.field.StringField;
import org.cheeryworks.liteql.skeleton.service.sql.SqlCustomizer;
import org.jooq.Condition;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.SelectLimitStep;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.cheeryworks.liteql.skeleton.query.enums.ConditionClause.IN;
import static org.cheeryworks.liteql.skeleton.query.enums.ConditionClause.NOT_NULL;
import static org.cheeryworks.liteql.skeleton.query.enums.ConditionClause.NULL;

public class JooqUtil {

    public static final Map<Integer, Class> SUPPORTED_DATA_TYPES = new HashMap<>();

    static {
        SUPPORTED_DATA_TYPES.put(Types.VARCHAR, String.class);
        SUPPORTED_DATA_TYPES.put(Types.BIGINT, Long.class);
        SUPPORTED_DATA_TYPES.put(Types.INTEGER, Integer.class);
        SUPPORTED_DATA_TYPES.put(Types.BOOLEAN, Boolean.class);
        SUPPORTED_DATA_TYPES.put(Types.NUMERIC, BigDecimal.class);
        SUPPORTED_DATA_TYPES.put(Types.TIMESTAMP, Timestamp.class);
        SUPPORTED_DATA_TYPES.put(Types.CLOB, String.class);
        SUPPORTED_DATA_TYPES.put(Types.BLOB, byte[].class);
    }

    public static DataType<String> getStringDataType() {
        return SQLDataType.VARCHAR(StringField.DEFAULT_LENGTH);
    }

    public static DataType<String> getStringDataType(boolean nullable) {
        return getStringDataType().nullable(nullable);
    }

    public static DataType<String> getStringDataType(boolean nullable, int length) {
        return getStringDataType(nullable).length(length);
    }

    public static DataType<Long> getLongDataType() {
        return SQLDataType.BIGINT;
    }

    public static DataType<Long> getLongDataType(boolean nullable) {
        return getLongDataType().nullable(nullable);
    }

    public static DataType<Integer> getIntegerDataType() {
        return SQLDataType.INTEGER;
    }

    public static DataType<Integer> getIntegerDataType(boolean nullable) {
        return getIntegerDataType().nullable(nullable);
    }

    public static DataType<Boolean> getBooleanDataType() {
        return SQLDataType.BOOLEAN.nullable(false);
    }

    public static DataType<BigDecimal> getBigDecimalDataType() {
        return SQLDataType.DECIMAL(DecimalField.DEFAULT_PRECISION, DecimalField.DEFAULT_SCALE);
    }

    public static DataType<BigDecimal> getBigDecimalDataType(boolean nullable) {
        return getBigDecimalDataType().nullable(nullable);
    }

    public static DataType<Timestamp> getTimestampDataType() {
        return SQLDataType.TIMESTAMP;
    }

    public static DataType<Timestamp> getTimestampDataType(boolean nullable) {
        return getTimestampDataType().nullable(nullable);
    }

    public static DataType<String> getClobDataType() {
        return SQLDataType.CLOB;
    }

    public static DataType<String> getClobDataType(boolean nullable) {
        return getClobDataType().nullable(nullable);
    }

    public static DataType<byte[]> getBlobDataType() {
        return SQLDataType.BLOB;
    }

    public static DataType<byte[]> getBlobDataType(boolean nullable) {
        return getBlobDataType().nullable(nullable);
    }

    public static <T> DataType<T> getDataType(Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return (DataType<T>) getStringDataType();
        } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return (DataType<T>) getLongDataType();
        } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return (DataType<T>) getIntegerDataType();
        } else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return (DataType<T>) getBooleanDataType();
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return (DataType<T>) getBigDecimalDataType();
        } else if (Date.class.isAssignableFrom(type)) {
            return (DataType<T>) getTimestampDataType();
        } else if (byte[].class.isAssignableFrom(type)) {
            return (DataType<T>) getBlobDataType();
        }

        throw new IllegalArgumentException(type.getName() + " not supported");
    }

    public static DataType<?> getDataType(ConditionType conditionType) {
        if (conditionType == null) {
            conditionType = ConditionType.String;
        }

        if (conditionType.equals(ConditionType.Integer)) {
            return getIntegerDataType();
        } else if (conditionType.equals(ConditionType.Boolean)) {
            return getBooleanDataType();
        } else if (conditionType.equals(ConditionType.Decimal)) {
            return getBigDecimalDataType();
        } else if (conditionType.equals(ConditionType.Timestamp)) {
            return getTimestampDataType();
        } else if (conditionType.equals(ConditionType.String)) {
            return getStringDataType();
        }

        throw new IllegalArgumentException(
                "Condition type " + conditionType.name() + " not mapping with Jooq DataType");
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

        if (database.equals(Database.MARIADB)) {
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
            return Database.MARIADB;
        }

        if (dialect.equals(SQLDialect.POSTGRES)) {
            return Database.POSTGRESQL;
        }

        return null;
    }

    public static String getPageSql(SelectLimitStep selectLimitStep, Pageable pageable) {
        return getPageSql(selectLimitStep, pageable.getPage() * pageable.getSize(), pageable.getSize());
    }

    public static String getPageSql(SelectLimitStep selectLimitStep, int start, int limit) {
        selectLimitStep.limit(start, limit);

        return selectLimitStep.getSQL();
    }

    public static Condition getCondition(
            DomainTypeDefinition domainTypeDefinition, String tableAlias,
            QueryConditions queryConditions, SqlCustomizer sqlCustomizer) {
        return getCondition(null, null, domainTypeDefinition, tableAlias, queryConditions, sqlCustomizer);
    }

    public static Condition getCondition(
            DomainTypeDefinition parentDomainTypeDefinition, String parentTableAlias,
            DomainTypeDefinition domainTypeDefinition, String tableAlias,
            QueryConditions queryConditions, SqlCustomizer sqlCustomizer) {
        Condition condition = null;

        TypeName domainTypeName = domainTypeDefinition.getTypeName();

        if (queryConditions != null && queryConditions.size() > 0) {
            for (QueryCondition queryCondition : queryConditions) {
                Condition currentCondition;

                if (queryCondition.getField() != null) {
                    checkField(domainTypeDefinition, queryCondition.getField());

                    if (!NULL.equals(queryCondition.getCondition())
                            && !NOT_NULL.equals(queryCondition.getCondition())
                            && !ConditionType.String.equals(queryCondition.getType())
                            && queryCondition.getValue() == null) {
                        throw new IllegalArgumentException(
                                "Value of condition can not be null, " + queryCondition.toString());
                    }

                    String leftClause = ((StringUtils.isNotBlank(tableAlias)) ? tableAlias + "." : "")
                            + sqlCustomizer.getColumnName(domainTypeName, queryCondition.getField());

                    org.jooq.Field field = (ConditionType.Field.equals(queryCondition.getType()))
                            ? DSL.field(leftClause)
                            : DSL.field(leftClause, JooqUtil.getDataType(queryCondition.getType()));

                    switch (queryCondition.getCondition()) {
                        case LESS_THAN:
                            currentCondition = field
                                    .lessThan(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        case LESS_OR_EQUALS:
                            currentCondition = field
                                    .lessOrEqual(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        case GREATER_THAN:
                            currentCondition = field
                                    .greaterThan(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        case GREATER_OR_EQUALS:
                            currentCondition = field
                                    .greaterOrEqual(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        case STARTS_WITH:
                            currentCondition = field
                                    .startsWith(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        case CONTAINS:
                            currentCondition = field
                                    .contains(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        case BETWEEN:
                            List<Object> values = (List) getConditionRightClause(
                                    parentDomainTypeDefinition,
                                    parentTableAlias,
                                    queryCondition,
                                    sqlCustomizer);

                            currentCondition = field
                                    .between(values.get(0)).and(values.get(1));
                            break;
                        case IN:
                            currentCondition = field
                                    .in(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        case NOT_EQUALS:
                            currentCondition = field
                                    .notEqual(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        case NULL:
                            currentCondition = field.isNull();
                            break;
                        case NOT_NULL:
                            currentCondition = field.isNotNull();
                            break;
                        case EQUALS:
                        case LENGTH:
                            currentCondition = field
                                    .eq(getConditionRightClause(
                                            parentDomainTypeDefinition,
                                            parentTableAlias,
                                            queryCondition,
                                            sqlCustomizer));
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Unsupported condition clause " + queryCondition.getCondition());
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
                                        parentDomainTypeDefinition, parentTableAlias, domainTypeDefinition, tableAlias,
                                        queryCondition.getConditions(), sqlCustomizer));
                                break;
                            default:
                                condition = condition.and(getCondition(
                                        parentDomainTypeDefinition, parentTableAlias, domainTypeDefinition, tableAlias,
                                        queryCondition.getConditions(), sqlCustomizer));
                                break;
                        }
                    } else {
                        switch (queryCondition.getOperator()) {
                            case OR:
                                condition = DSL.or(getCondition(
                                        parentDomainTypeDefinition, parentTableAlias, domainTypeDefinition, tableAlias,
                                        queryCondition.getConditions(), sqlCustomizer));
                                break;
                            default:
                                condition = DSL.and(getCondition(
                                        parentDomainTypeDefinition, parentTableAlias, domainTypeDefinition, tableAlias,
                                        queryCondition.getConditions(), sqlCustomizer));
                                break;
                        }
                    }
                }
            }
        }

        return condition;
    }

    private static Object getConditionRightClause(
            DomainTypeDefinition parentDomainTypeDefinition, String parentTableAlias,
            QueryCondition queryCondition, SqlCustomizer sqlCustomizer) {
        if (ConditionType.Field.equals(queryCondition.getType())) {
            checkField(parentDomainTypeDefinition, queryCondition.getValue().toString());

            return DSL.field(
                    ((parentTableAlias != null) ? parentTableAlias + "." : "")
                            + sqlCustomizer.getColumnName(
                            parentDomainTypeDefinition.getTypeName(), queryCondition.getValue().toString()));
        }

        return transformValue(queryCondition);
    }

    public static void checkField(DomainTypeDefinition domainTypeDefinition, String fieldName) {
        boolean exist = false;

        for (Field field : domainTypeDefinition.getFields()) {
            if (field.getName().equals(fieldName)) {
                exist = true;
            }
        }

        if (!exist) {
            throw new IllegalArgumentException(
                    "Field " + fieldName + " not defined in " + domainTypeDefinition.getTypeName().getFullname());
        }
    }

    private static Object transformValue(QueryCondition queryCondition) {
        if (queryCondition.getValue() instanceof List) {
            List<Object> transformedValues = new ArrayList<>();
            for (Object value : (List) queryCondition.getValue()) {
                transformedValues.add(value);
            }

            if (IN.equals(queryCondition.getCondition()) && transformedValues.size() > 500) {
                throw new IllegalArgumentException("Value number of condition clause[IN] more than 500");
            }

            return transformedValues;
        } else {
            if (ConditionType.String.equals(queryCondition.getType()) && queryCondition.getValue() == null) {
                return "";
            }

            return queryCondition.getValue();
        }
    }

}
