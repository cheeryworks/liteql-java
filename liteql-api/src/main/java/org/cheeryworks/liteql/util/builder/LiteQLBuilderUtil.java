package org.cheeryworks.liteql.util.builder;

import org.cheeryworks.liteql.model.enums.ConditionClause;
import org.cheeryworks.liteql.model.enums.ConditionOperator;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.enums.Direction;
import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.read.sort.QuerySort;
import org.cheeryworks.liteql.util.builder.read.LiteQLReadQueryField;
import org.cheeryworks.liteql.util.builder.save.LiteQLSaveField;

import java.util.Arrays;
import java.util.List;

public final class LiteQLBuilderUtil {

    public static LiteQLReadQueryField field(String field) {
        return field(field, null);
    }

    public static LiteQLReadQueryField field(String field, String alias) {
        return new LiteQLReadQueryField(field, alias);
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

    public static LiteQLSaveField saveField(String name, Object value) {
        return new LiteQLSaveField(name, value);
    }

    public static List<LiteQLQueryReference> references(LiteQLQueryReference... liteQLQueryReferences) {
        return Arrays.asList(liteQLQueryReferences);
    }

    public static LiteQLQueryReference reference(String source, String destination) {
        return new LiteQLQueryReference(source, destination);
    }

}
