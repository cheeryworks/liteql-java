package org.cheeryworks.liteql.service.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class HierarchicalEntityUtil {

    public static final String SORT_CODE_FIELD_NAME = "sortCode";
    public static final String PARENT_ID_FIELD_NAME = "parentId";

    public static List<Map<String, Object>> sortInTree(
            List<Map<String, Object>> dataSet) {
        List<Map<String, Object>> sortedMapsInTree = new LinkedList<Map<String, Object>>();

        if (CollectionUtils.isNotEmpty(dataSet)) {
            Map<String, Object> firstRow = dataSet.get(0);

            Set<String> parentKeyFieldNames = new HashSet<String>();

            for (String fieldName : firstRow.keySet()) {
                if (fieldName.startsWith("parent") && !fieldName.equals(PARENT_ID_FIELD_NAME)) {
                    parentKeyFieldNames.add(fieldName);
                }
            }

            int priority = 1;
            long sortCode = 1001;
            for (Map<String, Object> data : dataSet) {
                boolean isRoot = false;
                for (String parentKeyFieldName : parentKeyFieldNames) {
                    Object parentKeyFieldValue = data.get(parentKeyFieldName);
                    if (parentKeyFieldValue == null || StringUtils.isBlank(parentKeyFieldValue.toString())) {
                        data.put("priority", priority);
                        data.put(SORT_CODE_FIELD_NAME, String.valueOf(sortCode));
                        data.put("leaf", true);
                        sortedMapsInTree.add(data);

                        priority++;
                        sortCode++;

                        isRoot = true;

                        break;
                    }
                }

                if (isRoot) {
                    generateSortCodeForMap(dataSet, data, parentKeyFieldNames);
                }
            }
        }

        return sortedMapsInTree;
    }

    private static void generateSortCodeForMap(
            List<Map<String, Object>> dataSet, Map<String, Object> sortedData, Set<String> parentKeyFieldNames) {
        int priority = 1;
        String parentSortCode = (String) sortedData.get(SORT_CODE_FIELD_NAME);
        long sortCode = 1001;
        for (Map<String, Object> data : dataSet) {
            boolean matched = true;

            for (String parentKeyFieldName : parentKeyFieldNames) {
                Object parentKeyFieldValue = data.get(parentKeyFieldName);

                if (parentKeyFieldValue == null
                        || StringUtils.isBlank(parentKeyFieldValue.toString())
                        || !parentKeyFieldValue.equals(
                        sortedData.get(getFieldNameByParentFieldName(parentKeyFieldName)))) {
                    matched = false;

                    break;
                }
            }

            if (matched) {
                data.put("priority", priority);
                data.put(SORT_CODE_FIELD_NAME, parentSortCode + sortCode);
                data.put("leaf", true);

                sortedData.put("leaf", false);

                addChild(sortedData, data);
                priority++;
                sortCode++;

                generateSortCodeForMap(
                        dataSet, data, parentKeyFieldNames);
            }
        }
    }

    public static void treeToList(
            List<Map<String, Object>> sortedDataInTree, List<Map<String, Object>> dataSet) {
        for (Map<String, Object> sortedMapInTree : sortedDataInTree) {
            dataSet.add(sortedMapInTree);
            if (sortedMapInTree.get("children") != null
                    && ((List<Map<String, Object>>) sortedMapInTree.get("children")).size() > 0) {
                treeToList((List<Map<String, Object>>) sortedMapInTree.get("children"), dataSet);
            }
        }

        for (Map<String, Object> data : dataSet) {
            data.remove("children");
        }
    }


    private static String getFieldNameByParentFieldName(String parentKeyFieldName) {
        return StringUtils.uncapitalize(StringUtils.removeStart(parentKeyFieldName, "parent"));
    }

    private static void addChild(Map<String, Object> parent, Map<String, Object> child) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");

        if (children == null) {
            children = new LinkedList<Map<String, Object>>();
            parent.put("children", children);
        }

        children.add(child);
    }

    public static List<Map<String, Object>> hierarchicalEntitiesInTreeForMap(
            List<Map<String, Object>> hierarchicalEntities, int expandLevel) {
        List<Map<String, Object>> hierarchicalEntitiesInTree = new LinkedList<Map<String, Object>>();

        if (hierarchicalEntities != null && hierarchicalEntities.size() > 0) {
            for (Map<String, Object> i : hierarchicalEntities) {
                boolean isRoot = true;

                String rootSortCode = (String) i.get(SORT_CODE_FIELD_NAME);

                if (rootSortCode.length() > 4) {
                    for (Map<String, Object> j : hierarchicalEntities) {
                        String childSortCode = (String) j.get(SORT_CODE_FIELD_NAME);
                        if (childSortCode.equals(rootSortCode.substring(0, rootSortCode.length() - 4))) {
                            isRoot = false;
                            break;
                        }
                    }
                }

                if (isRoot) {
                    i.put("leaf", true);
                    hierarchicalEntitiesInTree.add(i);
                }
            }

            hierarchicalEntitiesInTreeForMap(hierarchicalEntities, hierarchicalEntitiesInTree, expandLevel);
        }

        return hierarchicalEntitiesInTree;
    }

    private static void hierarchicalEntitiesInTreeForMap(
            List<Map<String, Object>> hierarchicalEntities,
            List<Map<String, Object>> hierarchicalEntitiesInTree,
            int expandLevel) {
        if (hierarchicalEntitiesInTree == null) {
            return;
        }

        for (Map<String, Object> hierarchicalEntityInTree : hierarchicalEntitiesInTree) {
            String parentSortCode = (String) hierarchicalEntityInTree.get(SORT_CODE_FIELD_NAME);
            for (Map<String, Object> hierarchicalEntity : hierarchicalEntities) {
                String childSortCode = (String) hierarchicalEntity.get(SORT_CODE_FIELD_NAME);
                if (childSortCode.startsWith(parentSortCode)
                        && childSortCode.length() == parentSortCode.length() + 4) {
                    hierarchicalEntityInTree.put("iconCls", "folder");
                    hierarchicalEntityInTree.put("leaf", false);

                    if (expandLevel == 0 || (parentSortCode.length() / 4 - 1) < expandLevel) {
                        hierarchicalEntityInTree.put("expanded", true);
                    }

                    hierarchicalEntity.put("leaf", true);

                    List<Map<String, Object>> children
                            = (List<Map<String, Object>>) hierarchicalEntityInTree.get("children");

                    if (children == null) {
                        children = new LinkedList<>();
                        hierarchicalEntityInTree.put("children", children);
                    }

                    children.add(hierarchicalEntity);
                }
            }

            hierarchicalEntitiesInTreeForMap(
                    hierarchicalEntities,
                    (List<Map<String, Object>>) hierarchicalEntityInTree.get("children"),
                    expandLevel);
        }
    }

}
