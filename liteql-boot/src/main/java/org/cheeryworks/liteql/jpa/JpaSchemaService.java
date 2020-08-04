package org.cheeryworks.liteql.jpa;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.graphql.annotation.GraphQLEntity;
import org.cheeryworks.liteql.graphql.annotation.GraphQLField;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.Schema;
import org.cheeryworks.liteql.schema.Trait;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.Type;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.VoidTrait;
import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.ReferenceField;
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

import static org.cheeryworks.liteql.service.schema.SchemaDefinition.VERSION_BASELINE_SUFFIX;
import static org.cheeryworks.liteql.service.schema.SchemaDefinition.VERSION_CONCAT;

public class JpaSchemaService extends DefaultSchemaService implements SchemaService {

    private static final SimpleDateFormat MIGRATION_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");

    private Map<TypeName, Map<String, String>> fieldNames = new HashMap<>();

    private Map<Class, Class> traitImplements = initTraitImplements();

    public JpaSchemaService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties, "classpath*:/liteql");

        Map<String, Set<Type>> typeNameWithinSchemas = getTypeWithinSchemas();

        for (Map.Entry<String, Set<Type>> typeNameWithinSchema : typeNameWithinSchemas.entrySet()) {
            for (Type type : typeNameWithinSchema.getValue()) {

                Type existType = getType(type.getTypeName());

                if (existType != null && !type.equals(existType)) {
                    throw new IllegalStateException(
                            "Definition of type [" + type.getTypeName() + "] not matched with it's JPA entity");
                }

                addType(type);
            }
        }
    }

    private Map<Class, Class> initTraitImplements() {
        ClassPathScanningCandidateComponentProvider traitInstanceScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        traitInstanceScanner.addIncludeFilter(new AnnotationTypeFilter(TraitInstance.class));

        Set<BeanDefinition> traitInstanceDefinitions = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            traitInstanceDefinitions.addAll(traitInstanceScanner.findCandidateComponents(packageToScan));
        }

        Map<Class, Class> traitImplements = new HashMap<>();

        for (BeanDefinition traitInstanceDefinition : traitInstanceDefinitions) {
            Class<?> domainJavaType = LiteQL.ClassUtils.getClass(traitInstanceDefinition.getBeanClassName());

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

        ClassPathScanningCandidateComponentProvider traitTypeScanner =
                new ClassPathScanningCandidateComponentProvider(false) {
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        AnnotationMetadata metadata = beanDefinition.getMetadata();
                        return metadata.isInterface();
                    }
                };

        traitTypeScanner.addIncludeFilter(new AssignableTypeFilter(Trait.class));

        Set<BeanDefinition> traitTypeBeanDefinitions = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            traitTypeBeanDefinitions.addAll(traitTypeScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition traitTypeBeanDefinition : traitTypeBeanDefinitions) {
            Class traitInterface = LiteQL.ClassUtils.getClass(traitTypeBeanDefinition.getBeanClassName());

            if (traitInterface.equals(Trait.class)) {
                continue;
            }

            TypeName typeName = LiteQL.SchemaUtils.getTypeName(traitInterface);

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

        ClassPathScanningCandidateComponentProvider jpaEntityScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        jpaEntityScanner.addIncludeFilter(new AnnotationTypeFilter(javax.persistence.Entity.class));

        Set<BeanDefinition> jpaEntityBeans = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            jpaEntityBeans.addAll(jpaEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition japEntityBean : jpaEntityBeans) {
            Class<? extends Trait> jpaEntityJavaType
                    = LiteQL.SchemaUtils.getTraitJavaType(japEntityBean.getBeanClassName());

            TypeName typeName = LiteQL.SchemaUtils.getTypeName(jpaEntityJavaType);

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

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            graphQLEntityBeans.addAll(graphQLEntityScanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition graphQLEntityBean : graphQLEntityBeans) {
            Class<?> graphQLEntityJavaType = LiteQL.ClassUtils.getClass(graphQLEntityBean.getBeanClassName());

            GraphQLEntity graphQLEntity = graphQLEntityJavaType.getAnnotation(GraphQLEntity.class);

            if (graphQLEntity != null && !graphQLEntity.extension().equals(Void.class) && !graphQLEntity.ignored()) {
                TypeName domainTypeName = LiteQL.SchemaUtils.getTypeName(graphQLEntity.extension());

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

    private DomainType entityTypeToDomainType(Class<? extends Trait> javaType, TypeName typeName) {
        DomainType domainType = new DomainType(typeName);

        GraphQLEntity graphQLEntity = javaType.getAnnotation(GraphQLEntity.class);

        if (graphQLEntity != null && graphQLEntity.ignored()) {
            domainType.setGraphQLType(false);
        }

        domainType.setVersion(LiteQL.SchemaUtils.getVersionOfTrait(javaType));

        performFieldsOfDomain(domainType, javaType);

        performUniquesAndIndexesOfDomain(domainType, javaType);

        performTraits(domainType, javaType);

        return domainType;
    }

    private TraitType traitInterfaceToTraitType(Class<? extends Trait> traitInterface, TypeName typeName) {
        TraitType traitType = new TraitType(typeName);

        traitType.setVersion(LiteQL.SchemaUtils.getVersionOfTrait(traitInterface));

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

        if (CollectionUtils.isEmpty(domainType.getFields())) {
            domainType.setFields(fields);
        } else {
            domainType.getFields().addAll(fields);
        }
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
                Set<String> columnNames
                        = LiteQL.StringUtils.convertDelimitedParameterToSetOfString(jpaIndex.columnList());

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
                        LiteQL.SchemaUtils.getTypeName(
                                this.traitImplements.get(referenceFieldAnnotation.targetDomainType())));
            } else {
                referenceField.setDomainTypeName(
                        LiteQL.SchemaUtils.getTypeName(referenceFieldAnnotation.targetDomainType()));
            }

            if (Collection.class.isAssignableFrom(fieldType)) {
                referenceField.setCollection(true);

                if (!referenceFieldAnnotation.mappedDomainType().equals(VoidTrait.class)
                        && !referenceFieldAnnotation.targetDomainType().equals(
                        referenceFieldAnnotation.mappedDomainType())) {
                    if (this.traitImplements.containsKey(referenceFieldAnnotation.targetDomainType())) {
                        referenceField.setMappedDomainTypeName(
                                LiteQL.SchemaUtils.getTypeName(
                                        this.traitImplements.get(referenceFieldAnnotation.targetDomainType())));
                    } else {
                        referenceField.setMappedDomainTypeName(
                                LiteQL.SchemaUtils.getTypeName(referenceFieldAnnotation.targetDomainType()));
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

        Set<Class<? extends Trait>> traitInterfaces = new TreeSet<>((o1, o2) -> {
            if (o1.isAssignableFrom(o2)) {
                return 0;
            } else {
                return -1;
            }
        });

        for (Class<?> javaTypeInterface : javaTypeInterfaces) {
            if (Trait.class.isAssignableFrom(javaTypeInterface)) {
                traitInterfaces.add((Class<? extends Trait>) javaTypeInterface);
            }
        }

        for (Class<? extends Trait> traitInterface : traitInterfaces) {
            TypeName typeName = LiteQL.SchemaUtils.getTypeName(traitInterface);

            if (typeName != null) {
                typeNames.add(typeName);
            }
        }

        if (CollectionUtils.isNotEmpty(typeNames)) {
            traitType.setTraits(typeNames);
        }
    }

    @Override
    protected void exportMigrations(
            DomainType domainType, Map<String, Migration> migrations, File typeDirectory) throws IOException {
        super.exportMigrations(domainType, migrations, typeDirectory);

        if (MapUtils.isEmpty(migrations) || migrations.size() == 1) {
            String migrationName = domainType.getVersion() +
                    VERSION_BASELINE_SUFFIX + VERSION_CONCAT +
                    MIGRATION_TIME_FORMAT.format(new Date()) +
                    LiteQL.Constants.WORD_CONCAT +
                    "create_" + domainType.getTypeName().getName();

            File migrationsDirectory = new File(typeDirectory + "/" + Schema.NAME_OF_MIGRATIONS_DIRECTORY);

            if (migrationsDirectory.exists()) {
                for (File migrationDefinitionFile : migrationsDirectory.listFiles()) {
                    if (migrationDefinitionFile.getName().startsWith(domainType.getVersion())) {
                        migrationDefinitionFile.delete();
                    }
                }
            }

            File migrationDefinitionFile = new File(
                    typeDirectory + "/" + Schema.NAME_OF_MIGRATIONS_DIRECTORY + "/" +
                            migrationName + Schema.SUFFIX_OF_CONFIGURATION_FILE);

            Migration migration = new Migration();
            migration.setName(migrationName);
            migration.setDomainTypeName(domainType.getTypeName());
            migration.setVersion(domainType.getVersion() + VERSION_BASELINE_SUFFIX);
            migration.setBaseline(true);

            CreateTypeMigrationOperation createTypeMigrationOperation = new CreateTypeMigrationOperation();
            createTypeMigrationOperation.setFields(domainType.getFields());
            createTypeMigrationOperation.setIndexes(domainType.getIndexes());
            createTypeMigrationOperation.setUniques(domainType.getUniques());

            migration.setOperations(Collections.singletonList(createTypeMigrationOperation));

            FileUtils.write(
                    migrationDefinitionFile, LiteQL.JacksonJsonUtils.toJson(migration) + "\n", StandardCharsets.UTF_8);
        }
    }

}
