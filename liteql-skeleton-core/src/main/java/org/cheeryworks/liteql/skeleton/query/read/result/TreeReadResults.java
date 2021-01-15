package org.cheeryworks.liteql.skeleton.query.read.result;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cheeryworks.liteql.skeleton.query.read.result.TreeReadResult.CHILDREN_FIELD_NAME;
import static org.cheeryworks.liteql.skeleton.query.read.result.TreeReadResult.LEAF_FIELD_NAME;
import static org.cheeryworks.liteql.skeleton.query.read.result.TreeReadResult.PARENT_ID_FIELD_NAME;
import static org.cheeryworks.liteql.skeleton.query.read.result.TreeReadResult.PRIORITY_FIELD_NAME;
import static org.cheeryworks.liteql.skeleton.query.read.result.TreeReadResult.SORT_CODE_FIELD_NAME;
import static org.cheeryworks.liteql.skeleton.schema.field.IdField.ID_FIELD_NAME;

public class TreeReadResults extends LinkedList<TreeReadResult> implements ReadResultsData<TreeReadResult> {

    public TreeReadResults(List<TreeReadResult> source) {
        super(source);
    }

    @Override
    public List<TreeReadResult> getData() {
        return this.stream().collect(Collectors.toList());
    }

    public static List<Map<String, Object>> sortInTree(
            List<Map<String, Object>> dataSet) {
        List<Map<String, Object>> sortedMapsInTree = new LinkedList<>();

        if (CollectionUtils.isNotEmpty(dataSet)) {
            Map<String, Object> firstRow = dataSet.get(0);

            Set<String> parentKeyFieldNames = new HashSet<>();

            for (String fieldName : firstRow.keySet()) {
                if (!fieldName.equals(PARENT_ID_FIELD_NAME)) {
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
                        data.put(PRIORITY_FIELD_NAME, priority);
                        data.put(SORT_CODE_FIELD_NAME, String.valueOf(sortCode));
                        data.put(LEAF_FIELD_NAME, true);
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
                        sortedData.get(ID_FIELD_NAME))) {
                    matched = false;

                    break;
                }
            }

            if (matched) {
                data.put(PRIORITY_FIELD_NAME, priority);
                data.put(SORT_CODE_FIELD_NAME, parentSortCode + sortCode);
                data.put(LEAF_FIELD_NAME, true);

                sortedData.put(LEAF_FIELD_NAME, false);

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
            if (sortedMapInTree.get(CHILDREN_FIELD_NAME) != null
                    && ((List<Map<String, Object>>) sortedMapInTree.get(CHILDREN_FIELD_NAME)).size() > 0) {
                treeToList((List<Map<String, Object>>) sortedMapInTree.get(CHILDREN_FIELD_NAME), dataSet);
            }
        }

        for (Map<String, Object> data : dataSet) {
            data.remove(CHILDREN_FIELD_NAME);
        }
    }

    private static void addChild(Map<String, Object> parent, Map<String, Object> child) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get(CHILDREN_FIELD_NAME);

        if (children == null) {
            children = new LinkedList<>();
            parent.put(CHILDREN_FIELD_NAME, children);
        }

        children.add(child);
    }

    public static TreeReadResults transformInTree(List<ReadResult> results) {
        List<TreeReadResult> hierarchicalEntities = hierarchicalEntitiesInTreeForMap(results);

        return new TreeReadResults(hierarchicalEntities);
    }

    private static List<TreeReadResult> hierarchicalEntitiesInTreeForMap(List<ReadResult> hierarchicalEntities) {
        List<TreeReadResult> hierarchicalEntitiesInTree = new LinkedList<>();

        if (hierarchicalEntities != null && hierarchicalEntities.size() > 0) {
            for (Map<String, Object> hierarchicalEntity : hierarchicalEntities) {
                boolean isRoot = true;

                String rootSortCode = (String) hierarchicalEntity.get(SORT_CODE_FIELD_NAME);

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
                    hierarchicalEntity.put(LEAF_FIELD_NAME, true);
                    hierarchicalEntitiesInTree.add(new TreeReadResult(hierarchicalEntity));
                }
            }

            hierarchicalEntitiesInTreeForMap(hierarchicalEntities, hierarchicalEntitiesInTree);
        }

        return hierarchicalEntitiesInTree;
    }

    private static void hierarchicalEntitiesInTreeForMap(
            List<ReadResult> hierarchicalEntities, List<TreeReadResult> hierarchicalEntitiesInTree) {
        if (hierarchicalEntitiesInTree == null) {
            return;
        }

        for (TreeReadResult hierarchicalEntityInTree : hierarchicalEntitiesInTree) {
            String parentSortCode = (String) hierarchicalEntityInTree.get(SORT_CODE_FIELD_NAME);
            for (Map<String, Object> hierarchicalEntity : hierarchicalEntities) {
                String childSortCode = (String) hierarchicalEntity.get(SORT_CODE_FIELD_NAME);
                if (childSortCode.startsWith(parentSortCode)
                        && childSortCode.length() == parentSortCode.length() + 4) {
                    hierarchicalEntityInTree.put(LEAF_FIELD_NAME, false);

                    hierarchicalEntity.put(LEAF_FIELD_NAME, true);

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
                    hierarchicalEntityInTree.getChildren());
        }
    }

}
