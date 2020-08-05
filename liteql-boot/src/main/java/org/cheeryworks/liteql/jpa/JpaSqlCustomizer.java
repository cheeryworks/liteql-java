package org.cheeryworks.liteql.jpa;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.annotation.LiteQLReferenceField;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.DefaultSqlCustomizer;
import org.cheeryworks.liteql.util.LiteQL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JpaSqlCustomizer extends DefaultSqlCustomizer {

    private Map<TypeName, String> tableNames = new HashMap<>();

    private Map<TypeName, Map<String, String>> columnNames = new HashMap<>();

    private Map<TypeName, Map<String, String>> fieldNames = new HashMap<>();

    public JpaSqlCustomizer(SchemaService schemaService) {
        super(schemaService);

        ClassPathScanningCandidateComponentProvider jpaEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        jpaEntityScanner.addIncludeFilter(new AnnotationTypeFilter(javax.persistence.Entity.class));

        Set<BeanDefinition> jpaEntityBeans = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            jpaEntityBeans.addAll(jpaEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition japEntityBean : jpaEntityBeans) {
            Class<? extends TraitType> traitType
                    = LiteQL.SchemaUtils.getTraitType(japEntityBean.getBeanClassName());

            Table table = traitType.getAnnotation(Table.class);

            TypeName typeName = LiteQL.SchemaUtils.getTypeName(traitType);

            if (typeName != null) {
                if (table != null) {
                    tableNames.put(typeName, table.name());
                }

                Map<String, String> columnsOfType = new HashMap<>();

                Map<String, String> fieldsOfType = new HashMap<>();

                columnNames.put(typeName, columnsOfType);

                fieldNames.put(typeName, fieldsOfType);

                List<Field> javaFields = FieldUtils.getAllFieldsList(traitType);

                for (java.lang.reflect.Field javaField : javaFields) {
                    if (Modifier.isFinal(javaField.getModifiers()) || Modifier.isStatic(javaField.getModifiers())) {
                        continue;
                    }

                    Transient transientAnnotation = javaField.getAnnotation(Transient.class);

                    if (transientAnnotation != null) {
                        continue;
                    }

                    processAttribute(javaField.getName(), javaField, columnsOfType, fieldsOfType);
                }

                Method[] columnMethods
                        = MethodUtils.getMethodsWithAnnotation(traitType, Column.class, true, true);

                for (Method method : columnMethods) {
                    processAttribute(
                            BeanUtils.findPropertyForMethod(method).getName(), method, columnsOfType, fieldsOfType);
                }
            }
        }
    }

    private void processAttribute(
            String fieldName, AccessibleObject javaField,
            Map<String, String> columnsOfType, Map<String, String> fieldsOfType) {
        Column column = javaField.getAnnotation(Column.class);

        LiteQLReferenceField liteQLReferenceField = javaField.getAnnotation(LiteQLReferenceField.class);

        if (liteQLReferenceField != null && StringUtils.isNotBlank(liteQLReferenceField.name())) {
            fieldName = liteQLReferenceField.name();
        }

        if (column != null && StringUtils.isNotBlank(column.name())) {
            columnsOfType.put(fieldName, column.name());
            fieldsOfType.put(column.name(), fieldName);
        }
    }

    @Override
    public String getTableName(TypeName domainTypeName) {
        String tableName = tableNames.get(domainTypeName);

        if (StringUtils.isBlank(tableName)) {
            return super.getTableName(domainTypeName);
        }

        return tableName;
    }

    @Override
    public String getColumnName(TypeName domainTypeName, String fieldName) {
        String columnName = null;

        if (columnNames.get(domainTypeName) != null) {
            columnName = columnNames.get(domainTypeName).get(fieldName);
        }

        if (StringUtils.isBlank(columnName)) {
            return super.getColumnName(domainTypeName, fieldName);
        }

        return columnName;
    }

}
