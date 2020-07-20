package org.cheeryworks.liteql.service.util;

import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.query.read.result.ReadResult;
import org.cheeryworks.liteql.model.query.read.result.TreeReadResult;
import org.cheeryworks.liteql.model.query.read.result.TreeReadResults;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.service.query.condition.ConditionValueConverter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

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
            case Long:
                return Long.class;
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
