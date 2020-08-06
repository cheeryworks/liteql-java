package org.cheeryworks.liteql.jpa;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.graphql.annotation.GraphQLField;
import org.cheeryworks.liteql.graphql.annotation.GraphQLType;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.Schema;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.schema.TypeDefinition;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.VoidTraitType;
import org.cheeryworks.liteql.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.schema.annotation.LiteQLReferenceField;
import org.cheeryworks.liteql.schema.annotation.LiteQLTraitInstance;
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
import org.cheeryworks.liteql.schema.index.IndexDefinition;
import org.cheeryworks.liteql.schema.index.UniqueDefinition;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.schema.migration.operation.CreateTypeMigrationOperation;
import org.cheeryworks.liteql.service.schema.DefaultSchemaService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.util.LiteQL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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

import static org.cheeryworks.liteql.service.schema.SchemaMetadata.VERSION_BASELINE_SUFFIX;
import static org.cheeryworks.liteql.service.schema.SchemaMetadata.VERSION_CONCAT;

public class JpaSchemaService extends DefaultSchemaService implements SchemaService {

    private static final SimpleDateFormat MIGRATION_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");

    private Map<TypeName, Map<String, String>> fieldNames = new HashMap<>();

    private Map<Class, Class> traitImplements = initTraitImplements();

    public JpaSchemaService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties, "classpath*:/" + LiteQL.Constants.SCHEMA_DEFINITION_CLASSPATH_ROOT);

        Map<String, Set<TypeDefinition>> typeDefinitonWithinSchemas = getTypeDefinitionWithinSchemas();

        for (Map.Entry<String, Set<TypeDefinition>> typeDefinitionWithinSchema
                : typeDefinitonWithinSchemas.entrySet()) {
            for (TypeDefinition typeDefinition : typeDefinitionWithinSchema.getValue()) {

                TypeDefinition existTypeDefinition = getType(typeDefinition.getTypeName());

                if (existTypeDefinition != null && !typeDefinition.equals(existTypeDefinition)) {
                    throw new IllegalStateException(
                            "Definition of type [" + typeDefinition.getTypeName() + "]" +
                                    " not matched with it's JPA entity");
                }

                addType(typeDefinition);
            }
        }
    }

    private Map<Class, Class> initTraitImplements() {
        ClassPathScanningCandidateComponentProvider traitInstanceScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        traitInstanceScanner.addIncludeFilter(new AnnotationTypeFilter(LiteQLTraitInstance.class));

        Set<BeanDefinition> traitInstanceDefinitions = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            traitInstanceDefinitions.addAll(traitInstanceScanner.findCandidateComponents(packageToScan));
        }

        Map<Class, Class> traitImplements = new HashMap<>();

        for (BeanDefinition traitInstanceDefinition : traitInstanceDefinitions) {
            Class<?> domainJavaType = LiteQL.ClassUtils.getClass(traitInstanceDefinition.getBeanClassName());

            LiteQLTraitInstance liteQLTraitInstance = domainJavaType.getAnnotation(LiteQLTraitInstance.class);

            if (!liteQLTraitInstance.implement().equals(Void.class)) {
                if (traitImplements.containsKey(liteQLTraitInstance.implement())) {
                    throw new IllegalStateException(
                            "Duplicated implements of"
                                    + " [" + liteQLTraitInstance.implement().getName() + "]"
                                    + " in different package");
                } else {
                    traitImplements.put(liteQLTraitInstance.implement(), domainJavaType);
                }
            }
        }

        return Collections.unmodifiableMap(traitImplements);
    }

    private Map<String, Set<TypeDefinition>> getTypeDefinitionWithinSchemas() {
        Map<String, Set<TypeDefinition>> typeDefinitionWithinSchemas = new HashMap<>();

        ClassPathScanningCandidateComponentProvider traitTypeScanner =
                new ClassPathScanningCandidateComponentProvider(false) {
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        AnnotationMetadata metadata = beanDefinition.getMetadata();
                        return metadata.isInterface();
                    }
                };

        traitTypeScanner.addIncludeFilter(new AssignableTypeFilter(TraitType.class));

        Set<BeanDefinition> traitTypeBeanDefinitions = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            traitTypeBeanDefinitions.addAll(traitTypeScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition traitTypeBeanDefinition : traitTypeBeanDefinitions) {
            Class traitInterface = LiteQL.ClassUtils.getClass(traitTypeBeanDefinition.getBeanClassName());

            if (traitInterface.equals(TraitType.class)) {
                continue;
            }

            TypeName typeName = LiteQL.SchemaUtils.getTypeName(traitInterface);

            if (typeName != null) {
                Set<TypeDefinition> typeDefinitionWithinSchema =
                        typeDefinitionWithinSchemas.get(typeName.getSchema());

                if (typeDefinitionWithinSchema == null) {
                    typeDefinitionWithinSchema = new LinkedHashSet<>();
                    typeDefinitionWithinSchemas.put(typeName.getSchema(), typeDefinitionWithinSchema);
                }

                TraitTypeDefinition traitTypeDefinition =
                        traitInterfaceToTraitTypeDefinition(traitInterface, typeName);

                typeDefinitionWithinSchema.add(traitTypeDefinition);
            }
        }

        ClassPathScanningCandidateComponentProvider jpaEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        jpaEntityScanner.addIncludeFilter(new AnnotationTypeFilter(javax.persistence.Entity.class));

        Set<BeanDefinition> jpaEntityBeans = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            jpaEntityBeans.addAll(jpaEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition japEntityBean : jpaEntityBeans) {
            Class<? extends TraitType> traitType
                    = LiteQL.SchemaUtils.getTraitJavaType(japEntityBean.getBeanClassName());

            TypeName typeName = LiteQL.SchemaUtils.getTypeName(traitType);

            if (typeName != null) {
                Set<TypeDefinition> typeDefinitionWithinSchema = typeDefinitionWithinSchemas.get(typeName.getSchema());

                if (typeDefinitionWithinSchema == null) {
                    typeDefinitionWithinSchema = new LinkedHashSet<>();
                    typeDefinitionWithinSchemas.put(typeName.getSchema(), typeDefinitionWithinSchema);
                }

                DomainTypeDefinition domainTypeDefinition = entityTypeToDomainTypeDefinition(traitType, typeName);

                typeDefinitionWithinSchema.add(domainTypeDefinition);
            }
        }

        ClassPathScanningCandidateComponentProvider graphQLEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        graphQLEntityScanner.addIncludeFilter(new AnnotationTypeFilter(GraphQLType.class));

        Set<BeanDefinition> graphQLEntityBeans = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            graphQLEntityBeans.addAll(graphQLEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition graphQLEntityBean : graphQLEntityBeans) {
            Class<?> graphQLEntityJavaType = LiteQL.ClassUtils.getClass(graphQLEntityBean.getBeanClassName());

            GraphQLType graphQLType = graphQLEntityJavaType.getAnnotation(GraphQLType.class);

            if (graphQLType != null && !graphQLType.extension().equals(Void.class) && !graphQLType.ignored()) {
                TypeName domainTypeName = LiteQL.SchemaUtils.getTypeName(graphQLType.extension());

                Set<TypeDefinition> domainTypeDefinitions = typeDefinitionWithinSchemas.get(domainTypeName.getSchema());

                for (TypeDefinition domainTypeDefinition : domainTypeDefinitions) {
                    if (domainTypeDefinition.getTypeName().equals(domainTypeName)) {
                        performFieldsOfDomainType((DomainTypeDefinition) domainTypeDefinition, graphQLEntityJavaType);
                        break;
                    }
                }
            }
        }

        return typeDefinitionWithinSchemas;
    }

    private DomainTypeDefinition entityTypeToDomainTypeDefinition(
            Class<? extends TraitType> traitType, TypeName typeName) {
        DomainTypeDefinition domainTypeDefinition = new DomainTypeDefinition(typeName);

        GraphQLType graphQLType = traitType.getAnnotation(GraphQLType.class);

        if (graphQLType != null && graphQLType.ignored()) {
            domainTypeDefinition.setGraphQLType(false);
        }

        domainTypeDefinition.setVersion(LiteQL.SchemaUtils.getVersionOfTrait(traitType));

        performFieldsOfDomainType(domainTypeDefinition, traitType);

        performUniquesAndIndexesOfDomainType(domainTypeDefinition, traitType);

        performTraits(domainTypeDefinition, traitType);

        return domainTypeDefinition;
    }

    private TraitTypeDefinition traitInterfaceToTraitTypeDefinition(
            Class<? extends TraitType> traitType, TypeName typeName) {
        TraitTypeDefinition traitTypeDefinition = new TraitTypeDefinition(typeName);

        traitTypeDefinition.setVersion(LiteQL.SchemaUtils.getVersionOfTrait(traitType));

        performFieldsOfTrait(traitTypeDefinition, traitType);

        performTraits(traitTypeDefinition, traitType);

        return traitTypeDefinition;
    }

    private void performFieldsOfDomainType(
            DomainTypeDefinition domainTypeDefinition, Class<?> javaType) {
        Set<Field> fields = new LinkedHashSet<>();

        List<java.lang.reflect.Field> javaFields = FieldUtils.getAllFieldsList(javaType);

        Method[] columnMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, Column.class, true, true);

        Method[] lobMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, Lob.class, true, true);

        Method[] graphQLFieldMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, GraphQLField.class, true, true);

        Method[] referenceFieldMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, LiteQLReferenceField.class, true, true);

        Map<String, String> fieldsOfType = new HashMap<>();

        fieldNames.put(domainTypeDefinition.getTypeName(), fieldsOfType);

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

            LiteQLReferenceField liteQLReferenceFieldAnnotation
                    = getAnnotation(referenceFieldMethods, javaFieldName, javaField, LiteQLReferenceField.class);

            Field field = getField(
                    javaType, javaFieldName, javaField.getType(),
                    columnAnnotation, lobAnnotation, graphQLFieldAnnotation, liteQLReferenceFieldAnnotation);

            if (columnAnnotation != null && StringUtils.isNotBlank(columnAnnotation.name())) {
                fieldsOfType.put(columnAnnotation.name(), field.getName());
            }

            fields.add(field);
        }

        if (CollectionUtils.isEmpty(domainTypeDefinition.getFields())) {
            domainTypeDefinition.setFields(fields);
        } else {
            domainTypeDefinition.getFields().addAll(fields);
        }
    }

    private <T extends Annotation> T getAnnotation(
            Method[] methods, String propertyName, AccessibleObject accessibleObject, Class<T> annotationClass) {
        T annotation = accessibleObject.getAnnotation(annotationClass);

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

    private void performUniquesAndIndexesOfDomainType(
            DomainTypeDefinition domainTypeDefinition, Class<? extends TraitType> javaType) {
        Table table = javaType.getAnnotation(Table.class);

        Set<UniqueDefinition> uniqueDefinitions = new LinkedHashSet<>();
        Set<IndexDefinition> indexDefinitions = new LinkedHashSet<>();

        if (table != null) {
            for (javax.persistence.Index jpaIndex : table.indexes()) {
                Set<String> columnNames
                        = LiteQL.StringUtils.convertDelimitedParameterToSetOfString(jpaIndex.columnList());

                Set<String> fieldNames = new LinkedHashSet<>();

                for (String columnName : columnNames) {
                    fieldNames.add(getFieldName(domainTypeDefinition.getTypeName(), columnName));
                }

                if (jpaIndex.unique()) {
                    UniqueDefinition uniqueDefinition = new UniqueDefinition();
                    uniqueDefinition.setFields(fieldNames);

                    uniqueDefinitions.add(uniqueDefinition);
                } else {
                    IndexDefinition indexDefinition = new IndexDefinition();
                    indexDefinition.setFields(fieldNames);

                    indexDefinitions.add(indexDefinition);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(uniqueDefinitions)) {
            domainTypeDefinition.setUniques(uniqueDefinitions);
        }

        if (CollectionUtils.isNotEmpty(indexDefinitions)) {
            domainTypeDefinition.setIndexes(indexDefinitions);
        }
    }

    private String getFieldName(TypeName domainTypeName, String columnName) {
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

    private void performFieldsOfTrait(
            TraitTypeDefinition traitTypeDefinition, Class<? extends TraitType> traitInterface) {
        Set<Field> fields = new LinkedHashSet<>();

        Method[] methods = traitInterface.getDeclaredMethods();

        Set<Method> sortedMethods = new TreeSet<>((o1, o2) -> {
            LiteQLFieldPosition liteQLFieldPosition1 = o1.getAnnotation(LiteQLFieldPosition.class);
            LiteQLFieldPosition liteQLFieldPosition2 = o2.getAnnotation(LiteQLFieldPosition.class);

            if (liteQLFieldPosition1 != null && liteQLFieldPosition2 != null) {
                return Integer.compare(liteQLFieldPosition1.value(), liteQLFieldPosition2.value());
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
                = MethodUtils.getMethodsWithAnnotation(traitInterface, LiteQLReferenceField.class, true, true);

        for (Method method : sortedMethods) {
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0) {
                String name = BeanUtils.findPropertyForMethod(method).getName();

                Column columnAnnotation = getAnnotation(columnMethods, name, method, Column.class);

                Lob lobAnnotation = getAnnotation(lobMethods, name, method, Lob.class);

                GraphQLField graphQLFieldAnnotation
                        = getAnnotation(graphQLFieldMethods, name, method, GraphQLField.class);

                LiteQLReferenceField liteQLReferenceFieldAnnotation
                        = getAnnotation(referenceFieldMethods, name, method, LiteQLReferenceField.class);

                Field field = getField(
                        traitInterface, name, method.getReturnType(),
                        columnAnnotation, lobAnnotation, graphQLFieldAnnotation, liteQLReferenceFieldAnnotation);

                fields.add(field);
            }
        }

        if (CollectionUtils.isEmpty(traitTypeDefinition.getFields())) {
            traitTypeDefinition.setFields(fields);
        } else {
            traitTypeDefinition.getFields().addAll(fields);
        }
    }

    private Field getField(
            Class<?> javaType, String name, Class<?> fieldType,
            Column columnAnnotation, Lob lobAnnotation,
            GraphQLField graphQLFieldAnnotation, LiteQLReferenceField liteQLReferenceFieldAnnotation) {
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
        } else if (fieldType.equals(String.class) && lobAnnotation == null && liteQLReferenceFieldAnnotation == null) {
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
        } else if (liteQLReferenceFieldAnnotation != null) {
            org.cheeryworks.liteql.schema.field.ReferenceField referenceField
                    = new org.cheeryworks.liteql.schema.field.ReferenceField(isGraphQLField);

            if (StringUtils.isNotBlank(liteQLReferenceFieldAnnotation.name())) {
                referenceField.setName(liteQLReferenceFieldAnnotation.name());
            }

            if (this.traitImplements.containsKey(liteQLReferenceFieldAnnotation.targetDomainType())) {
                referenceField.setDomainTypeName(
                        LiteQL.SchemaUtils.getTypeName(
                                this.traitImplements.get(liteQLReferenceFieldAnnotation.targetDomainType())));
            } else {
                referenceField.setDomainTypeName(
                        LiteQL.SchemaUtils.getTypeName(liteQLReferenceFieldAnnotation.targetDomainType()));
            }

            if (Collection.class.isAssignableFrom(fieldType)) {
                referenceField.setCollection(true);

                if (!liteQLReferenceFieldAnnotation.mappedDomainType().equals(VoidTraitType.class)
                        && !liteQLReferenceFieldAnnotation.targetDomainType().equals(
                        liteQLReferenceFieldAnnotation.mappedDomainType())) {
                    if (this.traitImplements.containsKey(liteQLReferenceFieldAnnotation.targetDomainType())) {
                        referenceField.setMappedDomainTypeName(
                                LiteQL.SchemaUtils.getTypeName(
                                        this.traitImplements.get(liteQLReferenceFieldAnnotation.targetDomainType())));
                    } else {
                        referenceField.setMappedDomainTypeName(
                                LiteQL.SchemaUtils.getTypeName(liteQLReferenceFieldAnnotation.targetDomainType()));
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

    private void performTraits(
            TraitTypeDefinition traitTypeDefinition, Class<? extends TraitType> javaType) {
        Set<TypeName> typeNames = new LinkedHashSet<>();

        List<Class<?>> javaTypeInterfaces = org.apache.commons.lang3.ClassUtils.getAllInterfaces(javaType);

        Set<Class<? extends TraitType>> traitTypes = new TreeSet<>((o1, o2) -> {
            if (o1.isAssignableFrom(o2)) {
                return 0;
            } else {
                return -1;
            }
        });

        for (Class<?> javaTypeInterface : javaTypeInterfaces) {
            if (TraitType.class.isAssignableFrom(javaTypeInterface)) {
                traitTypes.add((Class<? extends TraitType>) javaTypeInterface);
            }
        }

        for (Class<? extends TraitType> traitType : traitTypes) {
            TypeName typeName = LiteQL.SchemaUtils.getTypeName(traitType);

            if (typeName != null) {
                typeNames.add(typeName);
            }
        }

        if (CollectionUtils.isNotEmpty(typeNames)) {
            traitTypeDefinition.setTraits(typeNames);
        }
    }

    @Override
    protected void exportMigrations(
            DomainTypeDefinition domainTypeDefinition,
            Map<String, Migration> migrations, File typeDirectory) throws IOException {
        super.exportMigrations(domainTypeDefinition, migrations, typeDirectory);

        if (MapUtils.isEmpty(migrations) || migrations.size() == 1) {
            String migrationName = domainTypeDefinition.getVersion() +
                    VERSION_BASELINE_SUFFIX + VERSION_CONCAT +
                    MIGRATION_TIME_FORMAT.format(new Date()) +
                    LiteQL.Constants.WORD_CONCAT +
                    "create_" + domainTypeDefinition.getTypeName().getName();

            File migrationsDirectory = new File(typeDirectory + "/" + Schema.NAME_OF_MIGRATIONS_DIRECTORY);

            if (migrationsDirectory.exists()) {
                for (File migrationDefinitionFile : migrationsDirectory.listFiles()) {
                    if (migrationDefinitionFile.getName().startsWith(domainTypeDefinition.getVersion())) {
                        migrationDefinitionFile.delete();
                    }
                }
            }

            File migrationDefinitionFile = new File(
                    typeDirectory + "/" + Schema.NAME_OF_MIGRATIONS_DIRECTORY + "/" +
                            migrationName + Schema.SUFFIX_OF_CONFIGURATION_FILE);

            Migration migration = new Migration();
            migration.setName(migrationName);
            migration.setDomainTypeName(domainTypeDefinition.getTypeName());
            migration.setVersion(domainTypeDefinition.getVersion() + VERSION_BASELINE_SUFFIX);
            migration.setBaseline(true);

            CreateTypeMigrationOperation createTypeMigrationOperation = new CreateTypeMigrationOperation();
            createTypeMigrationOperation.setFields(domainTypeDefinition.getFields());
            createTypeMigrationOperation.setIndexes(domainTypeDefinition.getIndexes());
            createTypeMigrationOperation.setUniques(domainTypeDefinition.getUniques());

            migration.setOperations(Collections.singletonList(createTypeMigrationOperation));

            FileUtils.write(
                    migrationDefinitionFile,
                    LiteQL.JacksonJsonUtils.toJson(migration) + "\n",
                    StandardCharsets.UTF_8);
        }
    }

}
