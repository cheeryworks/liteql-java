package org.cheeryworks.liteql.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.read.result.ReadResult;
import org.cheeryworks.liteql.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.query.save.CreateQuery;
import org.cheeryworks.liteql.query.save.UpdateQuery;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class SqlQueryServiceUtil {

    public static Map<String, Class> getFieldDefinitions(DomainTypeDefinition domainTypeDefinition) {
        Map<String, Class> fieldDefinitions = new HashMap<>();

        for (Field field : domainTypeDefinition.getFields()) {
            fieldDefinitions.put(field.getName(), getDataType(field.getType()));
        }

        return fieldDefinitions;
    }

    private static Class getDataType(DataType dataType) {
        switch (dataType) {
            case Id:
            case Reference:
            case Clob:
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
            case Blob:
                return byte[].class;
            default:
                throw new IllegalArgumentException("Unsupported data type " + dataType);
        }
    }

    public static <T extends TraitType> T getTypedResult(Map<String, Object> data, Class<T> type) {
        try {
            T typedResult = type.getDeclaredConstructor().newInstance();

            for (Map.Entry<String, Object> sourceEntry : data.entrySet()) {
                String fieldName = sourceEntry.getKey();
                Object fieldValue = sourceEntry.getValue();

                if (fieldValue == null) {
                    continue;
                }

                FieldUtils.writeField(typedResult, fieldName, fieldValue, true);
            }

            return typedResult;
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static <T extends TraitType> List<T> getTypedResults(List<ReadResult> data, Class<T> type) {
        List<T> typedResults = new LinkedList<>();

        data.stream().map(readResult -> getTypedResult(readResult, type)).forEach(typedResults::add);

        return typedResults;
    }

    public static <T extends TraitType, S extends AbstractSaveQuery> S transformObjectToSaveQuery(
            T domainEntity, QueryType queryType, DomainTypeDefinition domainTypeDefinition) {
        Map<String, Object> domainObjectInMap = transformObjectToMap(domainEntity, domainTypeDefinition);

        AbstractSaveQuery saveQuery = QueryType.Create.equals(queryType) ? new CreateQuery() : new UpdateQuery();
        saveQuery.setDomainTypeName(LiteQL.SchemaUtils.getTypeName(domainEntity.getClass()));
        saveQuery.setData(domainObjectInMap);

        return (S) saveQuery;
    }

    private static <T extends TraitType> Map<String, Object> transformObjectToMap(
            T domainEntity, DomainTypeDefinition domainTypeDefinition) {
        Map<String, Object> domainEntityInMap = new HashMap<>();

        domainTypeDefinition.getFields().forEach(field -> {
            String propertyName = field.getName();

            if (DataType.Reference.equals(field.getType())) {
                propertyName = field.getName() + StringUtils.capitalize(IdField.ID_FIELD_NAME);
            }

            try {
                Object propertyValue = FieldUtils.readField(domainEntity, propertyName, true);

                domainEntityInMap.put(field.getName(), propertyValue);
            } catch (Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
        });

        return domainEntityInMap;
    }

}
