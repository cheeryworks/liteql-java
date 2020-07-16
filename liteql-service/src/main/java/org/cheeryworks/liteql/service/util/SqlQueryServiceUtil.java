package org.cheeryworks.liteql.service.util;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.query.read.result.ReadResult;
import org.cheeryworks.liteql.model.query.read.result.TreeReadResult;
import org.cheeryworks.liteql.model.query.read.result.TreeReadResults;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.service.jooq.datatype.JOOQDataType;
import org.cheeryworks.liteql.service.query.condition.ConditionValueConverter;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

public abstract class SqlQueryServiceUtil {

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

    public static String getFieldNameByColumnName(String columnName) {
        String fieldName = columnName.toLowerCase();

        String[] wordsOfColumnName = fieldName.split("_");

        StringBuffer fieldNameBuffer = new StringBuffer();

        for (int i = 0; i < wordsOfColumnName.length; i++) {
            fieldNameBuffer.append((i == 0) ? wordsOfColumnName[i] : StringUtils.capitalize(wordsOfColumnName[i]));
        }

        return fieldNameBuffer.toString();
    }

    public static String getColumnNameByFieldName(String fieldName) {
        return fieldName.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                "_"
        ).toLowerCase();
    }

    public static TreeReadResults transformInTree(
            List<ReadResult> results, Integer expandLevel) {
        if (expandLevel == null) {
            expandLevel = 0;
        }

        List<TreeReadResult> hierarchicalEntities = hierarchicalEntitiesInTreeForMap(results, expandLevel);

        return new TreeReadResults(hierarchicalEntities);
    }

    private static List<TreeReadResult> hierarchicalEntitiesInTreeForMap(
            List<ReadResult> hierarchicalEntities, int expandLevel) {
        List<TreeReadResult> hierarchicalEntitiesInTree = new LinkedList<>();

        if (hierarchicalEntities != null && hierarchicalEntities.size() > 0) {
            for (Map<String, Object> hierarchicalEntity : hierarchicalEntities) {
                boolean isRoot = true;

                String rootSortCode = (String) hierarchicalEntity.get(HierarchicalEntityUtil.SORT_CODE_FIELD_NAME);

                if (rootSortCode.length() > 4) {
                    for (Map<String, Object> j : hierarchicalEntities) {
                        String childSortCode = (String) j.get(HierarchicalEntityUtil.SORT_CODE_FIELD_NAME);
                        if (childSortCode.equals(rootSortCode.substring(0, rootSortCode.length() - 4))) {
                            isRoot = false;
                            break;
                        }
                    }
                }

                if (isRoot) {
                    hierarchicalEntity.put("leaf", true);
                    hierarchicalEntitiesInTree.add(new TreeReadResult(hierarchicalEntity));
                }
            }

            hierarchicalEntitiesInTreeForMap(hierarchicalEntities, hierarchicalEntitiesInTree, expandLevel);
        }

        return hierarchicalEntitiesInTree;
    }

    private static void hierarchicalEntitiesInTreeForMap(
            List<ReadResult> hierarchicalEntities,
            List<TreeReadResult> hierarchicalEntitiesInTree,
            int expandLevel) {
        if (hierarchicalEntitiesInTree == null) {
            return;
        }

        for (TreeReadResult hierarchicalEntityInTree : hierarchicalEntitiesInTree) {
            String parentSortCode = (String) hierarchicalEntityInTree.get(HierarchicalEntityUtil.SORT_CODE_FIELD_NAME);
            for (Map<String, Object> hierarchicalEntity : hierarchicalEntities) {
                String childSortCode = (String) hierarchicalEntity.get(HierarchicalEntityUtil.SORT_CODE_FIELD_NAME);
                if (childSortCode.startsWith(parentSortCode)
                        && childSortCode.length() == parentSortCode.length() + 4) {
                    hierarchicalEntityInTree.put("iconCls", "folder");
                    hierarchicalEntityInTree.put("leaf", false);

                    if (expandLevel == 0 || (parentSortCode.length() / 4 - 1) < expandLevel) {
                        hierarchicalEntityInTree.put("expanded", true);
                    }

                    hierarchicalEntity.put("leaf", true);

                    LinkedList<TreeReadResult> children = hierarchicalEntityInTree.getChildren();

                    if (children == null) {
                        children = new LinkedList<>();
                        hierarchicalEntityInTree.setChildren(children);
                    }

                    children.add(new TreeReadResult(hierarchicalEntity));
                }
            }

            hierarchicalEntitiesInTreeForMap(
                    hierarchicalEntities,
                    hierarchicalEntityInTree.getChildren(),
                    expandLevel);
        }
    }

    public static Condition getConditions(
            Configuration configuration, QueryConditions queryConditions, String parentTableAlias, String tableAlias) {
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
                            + SqlQueryServiceUtil.getColumnNameByFieldName(queryCondition.getField());

                    org.jooq.Field field = (ConditionType.Field.equals(queryCondition.getType()))
                            ? DSL.field(leftClause)
                            : DSL.field(leftClause, JOOQDataType.getDataType(queryCondition.getType()));

                    switch (queryCondition.getCondition()) {
                        case LESS_THAN:
                            currentCondition = field
                                    .lessThan(getConditionRightClause(
                                            queryCondition,
                                            parentTableAlias));
                            break;
                        case LESS_OR_EQUALS:
                            currentCondition = field
                                    .lessOrEqual(getConditionRightClause(
                                            queryCondition,
                                            parentTableAlias));
                            break;
                        case GREATER_THAN:
                            currentCondition = field
                                    .greaterThan(getConditionRightClause(
                                            queryCondition,
                                            parentTableAlias));
                            break;
                        case GREATER_OR_EQUALS:
                            currentCondition = field
                                    .greaterOrEqual(getConditionRightClause(
                                            queryCondition,
                                            parentTableAlias));
                            break;
                        case STARTS_WITH:
                            currentCondition = field
                                    .startsWith(getConditionRightClause(
                                            queryCondition,
                                            parentTableAlias));
                            break;
                        case CONTAINS:
                            currentCondition = field
                                    .contains(getConditionRightClause(
                                            queryCondition,
                                            parentTableAlias));
                            break;
                        case BETWEEN:
                            List<Object> values = (List) getConditionRightClause(
                                    queryCondition,
                                    parentTableAlias);

                            currentCondition = field
                                    .between(values.get(0)).and(values.get(1));
                            break;
                        case IN:
                            currentCondition = field
                                    .in(getConditionRightClause(
                                            queryCondition,
                                            parentTableAlias));
                            break;
                        case NOT_EQUALS:
                            currentCondition = field
                                    .notEqual(getConditionRightClause(
                                            queryCondition,
                                            parentTableAlias));
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
                                            queryCondition,
                                            parentTableAlias));
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
                                condition = condition.or(getConditions(
                                        configuration, queryCondition.getConditions(), parentTableAlias, tableAlias));
                                break;
                            default:
                                condition = condition.and(getConditions(
                                        configuration, queryCondition.getConditions(), parentTableAlias, tableAlias));
                                break;
                        }
                    } else {
                        switch (queryCondition.getOperator()) {
                            case OR:
                                condition = DSL.or(getConditions(
                                        configuration, queryCondition.getConditions(), parentTableAlias, tableAlias));
                                break;
                            default:
                                condition = DSL.and(getConditions(
                                        configuration, queryCondition.getConditions(), parentTableAlias, tableAlias));
                                break;
                        }
                    }
                }
            }
        }

        return condition;
    }

    private static Object getConditionRightClause(
            QueryCondition queryCondition, String parentTableAlias) {
        if (ConditionType.Field.equals(queryCondition.getType())) {
            return DSL.field(
                    ((parentTableAlias != null) ? parentTableAlias + "." : "")
                            + SqlQueryServiceUtil.getColumnNameByFieldName(queryCondition.getValue().toString()));
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

    public static Map<String, Class> getFieldDefinitions(DomainType domainType) {
        Map<String, Class> fieldDefinitions = new HashMap<>();

        for (Field field : domainType.getFields()) {
            fieldDefinitions.put(field.getName(), getDataType(field.getType()));
        }

        return fieldDefinitions;
    }

    private static Class getDataType(DataType dataType) {
        switch (dataType) {
            case Id:
            case Reference:
            case String:
                return String.class;
            case Integer:
                return Integer.class;
            case Boolean:
                return Boolean.class;
            case Decimal:
                return BigDecimal.class;
            case Timestamp:
                return Timestamp.class;
            default:
                throw new IllegalArgumentException("Unsupported data type " + dataType);
        }
    }

}
