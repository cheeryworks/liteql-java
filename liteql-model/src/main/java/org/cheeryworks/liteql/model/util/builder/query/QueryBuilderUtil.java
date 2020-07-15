package org.cheeryworks.liteql.model.util.builder.query;

import org.cheeryworks.liteql.model.enums.ConditionClause;
import org.cheeryworks.liteql.model.enums.ConditionOperator;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.enums.Direction;
import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.read.sort.QuerySort;
import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryFieldMetadata;
import org.cheeryworks.liteql.model.util.builder.query.save.SaveFieldMetadata;

import java.util.Arrays;
import java.util.List;

public final class QueryBuilderUtil {

    public static ReadQueryFieldMetadata field(String field) {
        return field(field, null);
    }

    public static ReadQueryFieldMetadata field(String field, String alias) {
        return new ReadQueryFieldMetadata(field, alias);
    }

    public static QueryCondition condition(String field, Object value) {
        return new QueryCondition(field, value);
    }

    public static QueryCondition condition(
            String field, ConditionClause condition, ConditionType type, Object value) {
        return new QueryCondition(field, condition, type, value);
    }

    public static QueryCondition condition(
            ConditionOperator operator, String field, ConditionClause condition, ConditionType type, Object value) {
        return new QueryCondition(operator, field, condition, type, value);
    }

    public static QuerySort sort(String field, Direction direction) {
        return new QuerySort(field, direction);
    }

    public static SaveFieldMetadata saveField(String name, Object value) {
        return new SaveFieldMetadata(name, value);
    }

    public static List<QueryReference> references(QueryReference... queryReferences) {
        return Arrays.asList(queryReferences);
    }

    public static QueryReference reference(String source, String destination) {
        return new QueryReference(source, destination);
    }

}
