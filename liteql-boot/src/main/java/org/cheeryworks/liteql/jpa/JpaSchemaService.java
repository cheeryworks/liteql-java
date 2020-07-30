package org.cheeryworks.liteql.jpa;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.model.VoidEntity;
import org.cheeryworks.liteql.boot.configuration.LiteQLSpringProperties;
import org.cheeryworks.liteql.graphql.annotation.GraphQLEntity;
import org.cheeryworks.liteql.graphql.annotation.GraphQLField;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.model.Entity;
import org.cheeryworks.liteql.model.Trait;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.Type;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.ReferenceField;
import org.cheeryworks.liteql.schema.annotation.ResourceDefinition;
import org.cheeryworks.liteql.schema.annotation.TraitInstance;
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
import org.cheeryworks.liteql.service.schema.DefaultSchemaService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JpaSchemaService extends DefaultSchemaService implements SchemaService {

    private Map<TypeName, Map<String, String>> fieldNames = new HashMap<>();

    private Map<Class, Class> traitImplements = initTraitImplements();

    public JpaSchemaService(LiteQLSpringProperties liteQLSpringProperties) {
        super(liteQLSpringProperties, "classpath*:/liteql");

        Map<String, Set<Type>> typeNameWithinSchemas = getTypeWithinSchemas();

        for (Map.Entry<String, Set<Type>> typeNameWithinSchema : typeNameWithinSchemas.entrySet()) {
            for (Type type : typeNameWithinSchema.getValue()) {
                addType(type);
            }
        }
    }

    private Map<Class, Class> initTraitImplements() {
        ClassPathScanningCandidateComponentProvider traitInstanceScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        traitInstanceScanner.addIncludeFilter(new AnnotationTypeFilter(TraitInstance.class));

        Set<BeanDefinition> traitInstanceDefinitions = new HashSet<>();

        for (String packageToScan : LiteQLUtil.getSchemaDefinitionPackages()) {
            traitInstanceDefinitions.addAll(traitInstanceScanner.findCandidateComponents(packageToScan));
        }

        Map<Class, Class> traitImplements = new HashMap<>();

        for (BeanDefinition traitInstanceDefinition : traitInstanceDefinitions) {
            Class<?> domainJavaType = LiteQLUtil.getClass(traitInstanceDefinition.getBeanClassName());

            TraitInstance traitInstance = domainJavaType.getAnnotation(TraitInstance.class);

            if (!traitInstance.implement().equals(Void.class)) {
                if (traitImplements.containsKey(traitInstance.implement())) {
                    throw new IllegalStateException(
                            "Duplicated implements of"
                                    + " [" + traitInstance.implement().getName() + "]"
                                    + " in different package");
                } else {
                    traitImplements.put(traitInstance.implement(), domainJavaType);
                }
            }
        }

        return Collections.unmodifiableMap(traitImplements);
    }

    private Map<String, Set<Type>> getTypeWithinSchemas() {
        Map<String, Set<Type>> typeWithinSchemas = new HashMap<>();

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

        for (String packageToScan : LiteQLUtil.getSchemaDefinitionPackages()) {
            resourceDefinitionBeans.addAll(resourceDefinitionScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition resourceDefinitionBean : resourceDefinitionBeans) {
            Class traitInterface = LiteQLUtil.getClass(resourceDefinitionBean.getBeanClassName());

            if (Entity.class.isAssignableFrom(traitInterface) && traitInterface.isInterface()) {
                TypeName typeName = Trait.getTypeName(traitInterface);

                if (typeName != null) {
                    Set<Type> typeWithinSchema = typeWithinSchemas.get(typeName.getSchema());

                    if (typeWithinSchema == null) {
                        typeWithinSchema = new LinkedHashSet<>();
                        typeWithinSchemas.put(typeName.getSchema(), typeWithinSchema);
                    }

                    TraitType traitType = traitInterfaceToTraitType(traitInterface, typeName);

                    typeWithinSchema.add(traitType);
                }
            }
        }

        ClassPathScanningCandidateComponentProvider jpaEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        jpaEntityScanner.addIncludeFilter(new AnnotationTypeFilter(javax.persistence.Entity.class));

        Set<BeanDefinition> jpaEntityBeans = new HashSet<>();

        for (String packageToScan : LiteQLUtil.getSchemaDefinitionPackages()) {
            jpaEntityBeans.addAll(jpaEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition japEntityBean : jpaEntityBeans) {
            Class<?> jpaEntityJavaType = LiteQLUtil.getClass(japEntityBean.getBeanClassName());

            TypeName typeName = Trait.getTypeName(jpaEntityJavaType);

            if (typeName != null) {
                Set<Type> typeWithinSchema = typeWithinSchemas.get(typeName.getSchema());

                if (typeWithinSchema == null) {
                    typeWithinSchema = new LinkedHashSet<>();
                    typeWithinSchemas.put(typeName.getSchema(), typeWithinSchema);
                }

                DomainType domainType = entityTypeToDomainType(jpaEntityJavaType, typeName);

                typeWithinSchema.add(domainType);
            }
        }

        ClassPathScanningCandidateComponentProvider graphQLEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        graphQLEntityScanner.addIncludeFilter(new AnnotationTypeFilter(GraphQLEntity.class));

        Set<BeanDefinition> graphQLEntityBeans = new HashSet<>();

        for (String packageToScan : LiteQLUtil.getSchemaDefinitionPackages()) {
            graphQLEntityBeans.addAll(graphQLEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition graphQLEntityBean : graphQLEntityBeans) {
            Class<?> graphQLEntityJavaType = LiteQLUtil.getClass(graphQLEntityBean.getBeanClassName());

            GraphQLEntity graphQLEntity = graphQLEntityJavaType.getAnnotation(GraphQLEntity.class);

            if (graphQLEntity != null && !graphQLEntity.extension().equals(Void.class) && !graphQLEntity.ignored()) {
                TypeName domainTypeName = Trait.getTypeName(graphQLEntity.extension());

                Set<Type> domainTypes = typeWithinSchemas.get(domainTypeName.getSchema());

                for (Type domainType : domainTypes) {
                    if (domainType.getTypeName().equals(domainTypeName)) {
                        performFieldsOfDomain((DomainType) domainType, graphQLEntityJavaType);
                        break;
                    }
                }
            }
        }

        return typeWithinSchemas;
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

        Map<String, String> fieldsOfType = new HashMap<>();

        fieldNames.put(domainType.getTypeName(), fieldsOfType);

        for (java.lang.reflect.Field javaField : javaFields) {
            if (Modifier.isFinal(javaField.getModifiers()) || Modifier.isStatic(javaField.getModifiers())) {
                continue;
            }

            Transient transientAnnotation = javaField.getAnnotation(Transient.class);

            if (transientAnnotation != null) {
                continue;
            }

            String javaFieldName = javaField.getName();

            Column columnAnnotation = getAnnotation(columnMethods, javaFieldName, javaField, Column.class);

            Lob lobAnnotation = getAnnotation(lobMethods, javaFieldName, javaField, Lob.class);

            GraphQLField graphQLFieldAnnotation
                    = getAnnotation(graphQLFieldMethods, javaFieldName, javaField, GraphQLField.class);

            ReferenceField referenceFieldAnnotation
                    = getAnnotation(referenceFieldMethods, javaFieldName, javaField, ReferenceField.class);

            Field field = getField(
                    javaType, javaFieldName, javaField.getType(),
                    columnAnnotation, lobAnnotation, graphQLFieldAnnotation, referenceFieldAnnotation);

            if (columnAnnotation != null && StringUtils.isNotBlank(columnAnnotation.name())) {
                fieldsOfType.put(columnAnnotation.name(), field.getName());
            }

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
        Table table = javaType.getAnnotation(Table.class);

        Set<Unique> uniques = new LinkedHashSet<>();
        Set<Index> indexes = new LinkedHashSet<>();

        if (table != null) {
            for (javax.persistence.Index jpaIndex : table.indexes()) {
                Set<String> columnNames = LiteQLUtil.convertDelimitedParameterToSetOfString(jpaIndex.columnList());

                Set<String> fieldNames = new LinkedHashSet<>();

                for (String columnName : columnNames) {
                    fieldNames.add(getFieldName(domainType.getTypeName(), columnName));
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

    public String getFieldName(TypeName domainTypeName, String columnName) {
        String fieldName = null;

        if (fieldNames.get(domainTypeName) != null) {
            fieldName = fieldNames.get(domainTypeName).get(columnName);
        }

        if (StringUtils.isBlank(fieldName)) {
            fieldName = columnName.toLowerCase();

            String[] wordsOfColumnName = fieldName.split("_");

            StringBuffer fieldNameBuffer = new StringBuffer();

            for (int i = 0; i < wordsOfColumnName.length; i++) {
                fieldNameBuffer.append((i == 0) ? wordsOfColumnName[i] : StringUtils.capitalize(wordsOfColumnName[i]));
            }

            return fieldNameBuffer.toString();
        }

        return fieldName;
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
                = MethodUtils.getMethodsWithAnnotation(traitInterface, GraphQLField.class, true, true);

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
        } else if (referenceFieldAnnotation != null) {
            org.cheeryworks.liteql.schema.field.ReferenceField referenceField
                    = new org.cheeryworks.liteql.schema.field.ReferenceField(isGraphQLField);

            if (StringUtils.isNotBlank(referenceFieldAnnotation.name())) {
                referenceField.setName(referenceFieldAnnotation.name());
            }

            if (this.traitImplements.containsKey(referenceFieldAnnotation.targetDomainType())) {
                referenceField.setDomainTypeName(
                        Trait.getTypeName(
                                this.traitImplements.get(referenceFieldAnnotation.targetDomainType())));
            } else {
                referenceField.setDomainTypeName(Trait.getTypeName(referenceFieldAnnotation.targetDomainType()));
            }

            if (Collection.class.isAssignableFrom(fieldType)) {
                referenceField.setCollection(true);

                if (!referenceFieldAnnotation.mappedDomainType().equals(VoidEntity.class)
                        && !referenceFieldAnnotation.targetDomainType().equals(
                        referenceFieldAnnotation.mappedDomainType())) {
                    if (this.traitImplements.containsKey(referenceFieldAnnotation.targetDomainType())) {
                        referenceField.setMappedDomainTypeName(
                                Trait.getTypeName(
                                        this.traitImplements.get(referenceFieldAnnotation.targetDomainType())));
                    } else {
                        referenceField.setMappedDomainTypeName(
                                Trait.getTypeName(referenceFieldAnnotation.targetDomainType()));
                    }
                }
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

}
