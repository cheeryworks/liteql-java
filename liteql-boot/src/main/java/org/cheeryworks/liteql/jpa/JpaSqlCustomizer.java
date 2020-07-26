package org.cheeryworks.liteql.jpa;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.Trait;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.annotation.ReferenceField;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.boot.configuration.LiteQLSpringProperties;
import org.cheeryworks.liteql.util.LiteQLUtil;
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

public class JpaSqlCustomizer implements SqlCustomizer {

    private Map<TypeName, String> tableNames = new HashMap<>();

    private Map<TypeName, Map<String, String>> columnNames = new HashMap<>();

    private Map<TypeName, Map<String, String>> fieldNames = new HashMap<>();

    private LiteQLProperties liteQLProperties = new LiteQLSpringProperties();

    public JpaSqlCustomizer(LiteQLProperties liteQLProperties) {
        if (liteQLProperties != null) {
            this.liteQLProperties = liteQLProperties;
        }

        ClassPathScanningCandidateComponentProvider jpaEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        jpaEntityScanner.addIncludeFilter(new AnnotationTypeFilter(javax.persistence.Entity.class));

        Set<BeanDefinition> jpaEntityBeans = new HashSet<>();

        for (String packageToScan : this.liteQLProperties.getPackagesToScan()) {
            jpaEntityBeans.addAll(jpaEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition japEntityBean : jpaEntityBeans) {
            Class<?> jpaEntityJavaType = LiteQLUtil.getClass(japEntityBean.getBeanClassName());

            Table table = jpaEntityJavaType.getAnnotation(Table.class);

            TypeName typeName = Trait.getTypeName(jpaEntityJavaType);

            if (typeName != null) {
                if (table != null) {
                    tableNames.put(typeName, table.name());
                }

                Map<String, String> columnsOfType = new HashMap<>();

                Map<String, String> fieldsOfType = new HashMap<>();

                columnNames.put(typeName, columnsOfType);

                fieldNames.put(typeName, fieldsOfType);

                List<Field> javaFields = FieldUtils.getAllFieldsList(jpaEntityJavaType);

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
                        = MethodUtils.getMethodsWithAnnotation(jpaEntityJavaType, Column.class, true, true);

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

        ReferenceField referenceField = javaField.getAnnotation(ReferenceField.class);

        if (referenceField != null) {
            fieldName = referenceField.name();
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
            return SqlCustomizer.super.getTableName(domainTypeName);
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
            return SqlCustomizer.super.getColumnName(domainTypeName, fieldName);
        }

        return columnName;
    }

    @Override
    public String getFieldName(TypeName domainTypeName, String columnName) {
        String fieldName = null;

        if (fieldNames.get(domainTypeName) != null) {
            fieldName = fieldNames.get(domainTypeName).get(columnName);
        }

        if (StringUtils.isBlank(fieldName)) {
            return SqlCustomizer.super.getFieldName(domainTypeName, columnName);
        }

        return fieldName;
    }

}
