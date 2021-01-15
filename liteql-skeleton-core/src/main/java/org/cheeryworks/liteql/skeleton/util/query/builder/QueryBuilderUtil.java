package org.cheeryworks.liteql.skeleton.util.query.builder;

import org.cheeryworks.liteql.skeleton.query.QueryCondition;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionClause;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionOperator;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionType;
import org.cheeryworks.liteql.skeleton.query.enums.Direction;
import org.cheeryworks.liteql.skeleton.query.read.sort.QuerySort;
import org.cheeryworks.liteql.skeleton.schema.TraitType;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.ReadQueryFieldMetadata;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.join.ReadQueryJoinFieldsBuilder;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.join.ReadQueryJoinMetadata;
import org.cheeryworks.liteql.skeleton.util.query.builder.save.SaveFieldMetadata;

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

    public static ReadQueryJoinFieldsBuilder join(Class<? extends TraitType> domainType) {
        return join(LiteQL.SchemaUtils.getTypeName(domainType));
    }

    public static ReadQueryJoinFieldsBuilder join(TypeName domainTypeName) {
        ReadQueryJoinMetadata readQueryJoinMetadata = new ReadQueryJoinMetadata();

        readQueryJoinMetadata.setDomainTypeName(domainTypeName);

        return new ReadQueryJoinFieldsBuilder(readQueryJoinMetadata);
    }
}
