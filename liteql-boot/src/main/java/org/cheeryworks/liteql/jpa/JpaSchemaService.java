package org.cheeryworks.liteql.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.ReferenceField;
import org.cheeryworks.liteql.schema.annotation.ResourceDefinition;
import org.cheeryworks.liteql.graphql.annotation.GraphQLEntity;
import org.cheeryworks.liteql.graphql.annotation.GraphQLField;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.Entity;
import org.cheeryworks.liteql.schema.Trait;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.field.AbstractField;
import org.cheeryworks.liteql.schema.field.AbstractNullableField;
import org.cheeryworks.liteql.schema.field.BlobField;
import org.cheeryworks.liteql.schema.field.BooleanField;
import org.cheeryworks.liteql.schema.field.ClobField;
import org.cheeryworks.liteql.schema.field.DecimalField;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;
import org.cheeryworks.liteql.schema.field.IntegerField;
import org.cheeryworks.liteql.schema.field.StringField;
import org.cheeryworks.liteql.schema.field.TimestampField;
import org.cheeryworks.liteql.schema.index.Index;
import org.cheeryworks.liteql.schema.index.Unique;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.cheeryworks.liteql.service.schema.DefaultSchemaService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.AbstractSqlService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JpaSchemaService extends AbstractSqlService implements SchemaService {

    private DefaultSchemaService repository;

    public JpaSchemaService(
            LiteQLProperties liteQLProperties, ObjectMapper objectMapper, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties, sqlCustomizer);

        this.repository = new DefaultSchemaService(liteQLProperties, objectMapper, "classpath*:/liteql");

        Map<String, Set<TypeName>> typeNameWithinSchemas = getTypeNameWithinSchemas();

        for (Map.Entry<String, Set<TypeName>> typeNameWithinSchema : typeNameWithinSchemas.entrySet()) {
            for (TypeName typeName : typeNameWithinSchema.getValue()) {
                repository.addType(typeName);
            }
        }
    }

    private Map<String, Set<TypeName>> getTypeNameWithinSchemas() {
        Map<String, Set<TypeName>> typeNameWithinSchemas = new HashMap<>();

        ClassPathScanningCandidateComponentProvider resourceDefinitionScanner =
                new ClassPathScanningCandidateComponentProvider(false) {
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        AnnotationMetadata metadata = beanDefinition.getMetadata();

                        if (metadata.isInterface()) {
                            return true;
                        }

                        return false;
                    }
                };

        resourceDefinitionScanner.addIncludeFilter(new AnnotationTypeFilter(ResourceDefinition.class));

        Set<BeanDefinition> resourceDefinitionBeans = new HashSet<>();

        for (String packageToScan : getLiteQLProperties().getPackagesToScan()) {
            resourceDefinitionBeans.addAll(resourceDefinitionScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition resourceDefinitionBean : resourceDefinitionBeans) {
            Class traitInterface = LiteQLUtil.getClass(resourceDefinitionBean.getBeanClassName());

            if (Entity.class.isAssignableFrom(traitInterface) && traitInterface.isInterface()) {
                TypeName typeName = Trait.getTypeName(traitInterface);

                if (typeName != null) {
                    Set<TypeName> typeNameWithinSchema = typeNameWithinSchemas.get(typeName.getSchema());

                    if (typeNameWithinSchema == null) {
                        typeNameWithinSchema = new LinkedHashSet<>();
                        typeNameWithinSchemas.put(typeName.getSchema(), typeNameWithinSchema);
                    }

                    TraitType traitType = traitInterfaceToTraitType(traitInterface, typeName);

                    typeNameWithinSchema.add(traitType);
                }
            }
        }

        ClassPathScanningCandidateComponentProvider jpaEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        jpaEntityScanner.addIncludeFilter(new AnnotationTypeFilter(javax.persistence.Entity.class));

        Set<BeanDefinition> jpaEntityBeans = new HashSet<>();

        for (String packageToScan : getLiteQLProperties().getPackagesToScan()) {
            jpaEntityBeans.addAll(jpaEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition japEntityBean : jpaEntityBeans) {
            Class<?> jpaEntityJavaType = LiteQLUtil.getClass(japEntityBean.getBeanClassName());

            TypeName typeName = Trait.getTypeName(jpaEntityJavaType);

            if (typeName != null) {
                Set<TypeName> typeNameWithinSchema = typeNameWithinSchemas.get(typeName.getSchema());

                if (typeNameWithinSchema == null) {
                    typeNameWithinSchema = new LinkedHashSet<>();
                    typeNameWithinSchemas.put(typeName.getSchema(), typeNameWithinSchema);
                }

                DomainType domainType = entityTypeToDomainType(jpaEntityJavaType, typeName);

                typeNameWithinSchema.add(domainType);
            }
        }

        ClassPathScanningCandidateComponentProvider graphQLEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        graphQLEntityScanner.addIncludeFilter(new AnnotationTypeFilter(GraphQLEntity.class));

        Set<BeanDefinition> graphQLEntityBeans = new HashSet<>();

        for (String packageToScan : getLiteQLProperties().getPackagesToScan()) {
            graphQLEntityBeans.addAll(graphQLEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition graphQLEntityBean : graphQLEntityBeans) {
            Class<?> graphQLEntityJavaType = LiteQLUtil.getClass(graphQLEntityBean.getBeanClassName());

            GraphQLEntity graphQLEntity = graphQLEntityJavaType.getAnnotation(GraphQLEntity.class);

            if (graphQLEntity != null && !graphQLEntity.extension().equals(Void.class) && !graphQLEntity.ignored()) {
                TypeName domainTypeName = Trait.getTypeName(graphQLEntity.extension());

                Set<TypeName> domainTypes = typeNameWithinSchemas.get(domainTypeName.getSchema());

                for (TypeName domainType : domainTypes) {
                    if (domainType.getName().equals(domainTypeName.getSchema())) {
                        performFieldsOfDomain((DomainType) domainType, graphQLEntityJavaType);
                        break;
                    }
                }
            }
        }

        return typeNameWithinSchemas;
    }

    private DomainType entityTypeToDomainType(Class<?> javaType, TypeName typeName) {
        DomainType domainType = new DomainType(typeName);

        GraphQLEntity graphQLEntity = javaType.getAnnotation(GraphQLEntity.class);

        if (graphQLEntity != null && graphQLEntity.ignored()) {
            domainType.setGraphQLType(false);
        }

        performFieldsOfDomain(domainType, javaType);

        performUniquesAndIndexesOfDomain(domainType, javaType);

        performTraits(domainType, javaType);

        return domainType;
    }

    private TraitType traitInterfaceToTraitType(Class traitInterface, TypeName typeName) {
        TraitType traitType = new TraitType(typeName);

        performFieldsOfTrait(traitType, traitInterface);

        performTraits(traitType, traitInterface);

        return traitType;
    }

    private void performFieldsOfDomain(DomainType domainType, Class<?> javaType) {
        Set<Field> fields = new LinkedHashSet<>();

        List<java.lang.reflect.Field> javaFields = FieldUtils.getAllFieldsList(javaType);

        Method[] columnMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, Column.class, true, true);

        Method[] lobMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, Lob.class, true, true);

        Method[] graphQLFieldMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, GraphQLEntity.class, true, true);

        Method[] referenceFieldMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, ReferenceField.class, true, true);

        for (java.lang.reflect.Field javaField : javaFields) {
            if (Modifier.isFinal(javaField.getModifiers()) || Modifier.isStatic(javaField.getModifiers())) {
                continue;
            }

            Transient transientAnnotation = javaField.getAnnotation(Transient.class);

            if (transientAnnotation != null) {
                continue;
            }

            String fieldName = javaField.getName();

            Column columnAnnotation = getAnnotation(columnMethods, fieldName, javaField, Column.class);

            Lob lobAnnotation = getAnnotation(lobMethods, fieldName, javaField, Lob.class);

            GraphQLField graphQLFieldAnnotation
                    = getAnnotation(graphQLFieldMethods, fieldName, javaField, GraphQLField.class);

            ReferenceField referenceFieldAnnotation
                    = getAnnotation(referenceFieldMethods, fieldName, javaField, ReferenceField.class);

            Field field = getField(
                    javaType, fieldName, javaField.getType(),
                    columnAnnotation, lobAnnotation, graphQLFieldAnnotation, referenceFieldAnnotation);

            fields.add(field);
        }

        domainType.setFields(fields);
    }

    private <T extends Annotation> T getAnnotation(
            Method[] methods, String propertyName, AccessibleObject javaField, Class<T> annotationClass) {
        T annotation = javaField.getAnnotation(annotationClass);

        if (annotation == null) {
            for (Method method : methods) {
                if (BeanUtils.findPropertyForMethod(method).getName().equalsIgnoreCase(propertyName)) {
                    annotation = method.getAnnotation(annotationClass);
                    break;
                }
            }
        }

        return annotation;
    }

    private void performUniquesAndIndexesOfDomain(DomainType domainType, Class<?> javaType) {
        Table table = AnnotationUtils.findAnnotation(javaType, Table.class);

        Set<Unique> uniques = new LinkedHashSet<>();
        Set<Index> indexes = new LinkedHashSet<>();

        if (table != null) {
            for (javax.persistence.Index jpaIndex : table.indexes()) {
                Set<String> columnNames = LiteQLUtil.convertDelimitedParameterToSetOfString(jpaIndex.columnList());

                Set<String> fieldNames = new LinkedHashSet<>();

                for (String columnName : columnNames) {
                    fieldNames.add(getSqlCustomizer().getFieldName(domainType, columnName));
                }

                if (jpaIndex.unique()) {
                    Unique unique = new Unique();
                    unique.setFields(fieldNames);

                    uniques.add(unique);
                } else {
                    Index index = new Index();
                    index.setFields(fieldNames);

                    indexes.add(index);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(uniques)) {
            domainType.setUniques(uniques);
        }

        if (CollectionUtils.isNotEmpty(indexes)) {
            domainType.setIndexes(indexes);
        }
    }

    private void performFieldsOfTrait(TraitType traitType, Class traitInterface) {
        Set<Field> fields = new LinkedHashSet<>();

        Method[] methods = traitInterface.getDeclaredMethods();

        Set<Method> sortedMethods = new TreeSet<>((o1, o2) -> {
            Position position1 = o1.getAnnotation(Position.class);
            Position position2 = o2.getAnnotation(Position.class);

            if (position1 != null && position2 != null) {
                return Integer.compare(position1.value(), position2.value());
            }

            return 1;
        });

        sortedMethods.addAll(Arrays.asList(methods));

        Method[] columnMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, Column.class, true, true);

        Method[] lobMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, Lob.class, true, true);

        Method[] graphQLFieldMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, GraphQLEntity.class, true, true);

        Method[] referenceFieldMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, ReferenceField.class, true, true);

        for (Method method : sortedMethods) {
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0) {
                String name = BeanUtils.findPropertyForMethod(method).getName();

                Column columnAnnotation = getAnnotation(columnMethods, name, method, Column.class);

                Lob lobAnnotation = getAnnotation(lobMethods, name, method, Lob.class);

                GraphQLField graphQLFieldAnnotation
                        = getAnnotation(graphQLFieldMethods, name, method, GraphQLField.class);

                ReferenceField referenceFieldAnnotation
                        = getAnnotation(referenceFieldMethods, name, method, ReferenceField.class);

                Field field = getField(
                        traitInterface, name, method.getReturnType(),
                        columnAnnotation, lobAnnotation, graphQLFieldAnnotation, referenceFieldAnnotation);

                fields.add(field);
            }
        }

        if (CollectionUtils.isEmpty(traitType.getFields())) {
            traitType.setFields(fields);
        } else {
            traitType.getFields().addAll(fields);
        }
    }

    private Field getField(
            Class<?> javaType, String name, Class<?> fieldType,
            Column columnAnnotation, Lob lobAnnotation,
            GraphQLField graphQLFieldAnnotation, ReferenceField referenceFieldAnnotation) {
        int length = 255;

        boolean nullable = true;

        if (columnAnnotation != null) {
            if (columnAnnotation.length() > 0) {
                length = columnAnnotation.length();
            }

            if (!columnAnnotation.nullable()) {
                nullable = false;
            }
        }

        AbstractField field = null;

        Boolean isGraphQLField = (graphQLFieldAnnotation != null && graphQLFieldAnnotation.ignore()) ? false : null;

        if (IdField.ID_FIELD_NAME.equalsIgnoreCase(name)) {
            IdField idField = new IdField();

            field = idField;
        } else if (fieldType.equals(String.class) && lobAnnotation == null && referenceFieldAnnotation == null) {
            StringField stringField = new StringField(isGraphQLField);

            stringField.setLength(length);

            field = stringField;
        } else if (fieldType.equals(Long.class) || fieldType.equals(Long.TYPE)) {
            StringField stringField = new StringField(isGraphQLField);

            stringField.setLength(length);

            field = stringField;
        } else if (fieldType.equals(Integer.class) || fieldType.equals(Integer.TYPE)) {
            IntegerField integerField = new IntegerField(isGraphQLField);

            field = integerField;
        } else if (Date.class.isAssignableFrom(fieldType)) {
            TimestampField timestampField = new TimestampField(isGraphQLField);

            field = timestampField;
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(Boolean.TYPE)) {
            BooleanField booleanField = new BooleanField(isGraphQLField);

            field = booleanField;
        } else if (fieldType.equals(BigDecimal.class)) {
            DecimalField decimalField = new DecimalField(isGraphQLField);

            field = decimalField;
        } else if (fieldType.equals(String.class) && lobAnnotation != null) {
            ClobField clobField = new ClobField(isGraphQLField);

            field = clobField;
        } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
            BlobField blobField = new BlobField(isGraphQLField);

            field = blobField;
        } else if (fieldType.equals(String.class) && referenceFieldAnnotation != null) {
            org.cheeryworks.liteql.schema.field.ReferenceField referenceField
                    = new org.cheeryworks.liteql.schema.field.ReferenceField(isGraphQLField);

            referenceField.setName(referenceFieldAnnotation.name());

            if (this.repository.getTraitImplements().containsKey(referenceFieldAnnotation.targetDomainType())) {
                referenceField.setDomainTypeName(
                        Trait.getTypeName(
                                this.repository.getTraitImplements()
                                        .get(referenceFieldAnnotation.targetDomainType())));
            } else {
                referenceField.setDomainTypeName(Trait.getTypeName(referenceFieldAnnotation.targetDomainType()));
            }

            field = referenceField;
        }

        if (field != null) {
            if (StringUtils.isBlank(field.getName())) {
                field.setName(name);
            }

            if (field instanceof AbstractNullableField && !nullable) {
                ((AbstractNullableField) field).setNullable(false);
            }
        }

        if (field == null) {
            throw new IllegalArgumentException("Can not parse field [" + name + "] of [" + javaType.getName() + "]");
        }

        return field;
    }

    private void performTraits(TraitType traitType, Class javaType) {
        Set<TypeName> typeNames = new LinkedHashSet<>();

        List<Class<?>> javaTypeInterfaces = ClassUtils.getAllInterfaces(javaType);

        Set<Class<?>> traitInterfaces = new TreeSet<>((o1, o2) -> {
            if (o1.isAssignableFrom(o2)) {
                return 0;
            } else {
                return -1;
            }
        });

        for (Class<?> javaTypeInterface : javaTypeInterfaces) {
            if (Entity.class.isAssignableFrom(javaTypeInterface)) {
                traitInterfaces.add(javaTypeInterface);
            }
        }

        for (Class<?> traitInterface : traitInterfaces) {
            TypeName typeName = Trait.getTypeName(traitInterface);

            if (typeName != null) {
                typeNames.add(typeName);
            }
        }

        if (CollectionUtils.isNotEmpty(typeNames)) {
            traitType.setTraits(typeNames);
        }
    }

    @Override
    public Set<String> getSchemaNames() {
        return this.repository.getSchemaNames();
    }

    @Override
    public Set<DomainType> getDomainTypes(String schemaName) {
        return this.repository.getDomainTypes(schemaName);
    }

    @Override
    public Set<TraitType> getTraitTypes(String schemaName) {
        return this.repository.getTraitTypes(schemaName);
    }

    @Override
    public DomainType getDomainType(TypeName typeName) {
        return this.repository.getDomainType(typeName);
    }

    @Override
    public TraitType getTraitType(TypeName typeName) {
        return this.repository.getTraitType(typeName);
    }

    @Override
    public Map<TypeName, Map<String, Migration>> getMigrations(String schemaName) {
        return this.repository.getMigrations(schemaName);
    }

}
