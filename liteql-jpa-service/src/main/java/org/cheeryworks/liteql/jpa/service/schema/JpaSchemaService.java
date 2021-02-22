package org.cheeryworks.liteql.jpa.service.schema;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.graphql.annotation.GraphQLField;
import org.cheeryworks.liteql.skeleton.schema.AbstractTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.Schema;
import org.cheeryworks.liteql.skeleton.schema.TraitType;
import org.cheeryworks.liteql.skeleton.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLReferenceField;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.field.IdField;
import org.cheeryworks.liteql.skeleton.schema.field.StringField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.AbstractField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.AbstractNullableField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultBlobField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultBooleanField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultClobField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultDecimalField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultIdField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultIntegerField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultLongField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultReferenceField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultStringField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.DefaultTimestampField;
import org.cheeryworks.liteql.skeleton.schema.index.IndexDefinition;
import org.cheeryworks.liteql.skeleton.schema.index.UniqueDefinition;
import org.cheeryworks.liteql.skeleton.schema.migration.Migration;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateTypeMigrationOperation;
import org.cheeryworks.liteql.skeleton.service.schema.DefaultSchemaService;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.reflections.Reflections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.cheeryworks.liteql.skeleton.schema.Schema.VERSION_BASELINE_SUFFIX;
import static org.cheeryworks.liteql.skeleton.schema.Schema.VERSION_CONCAT;

public class JpaSchemaService extends DefaultSchemaService implements SchemaService {

    private static final SimpleDateFormat MIGRATION_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");

    public JpaSchemaService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);
    }

    @Override
    protected Map<String, Set<TypeDefinition>> getAdditionalTypeDefinitionWithinSchemas() {
        Map<String, Set<TypeDefinition>> typeDefinitionWithinSchemas = new HashMap<>();

        Set<Class<?>> jpaEntityJavaTypes = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            Reflections reflections = new Reflections(packageToScan);

            jpaEntityJavaTypes.addAll(reflections.getTypesAnnotatedWith(Entity.class));
        }

        javaTypesToDomainTypeDefinitions(jpaEntityJavaTypes, typeDefinitionWithinSchemas);

        return typeDefinitionWithinSchemas;
    }

    @Override
    protected void performFieldsOfJavaType(AbstractTypeDefinition typeDefinition, Class<?> javaType) {
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

        Map<String, String> columnNameFieldNameMapping = new HashMap<>();

        for (java.lang.reflect.Field javaField : javaFields) {
            if (Modifier.isFinal(javaField.getModifiers())
                    || Modifier.isStatic(javaField.getModifiers())
                    || Modifier.isTransient(javaField.getModifiers())) {
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
                columnNameFieldNameMapping.put(columnAnnotation.name(), field.getName());
            }

            fields.add(field);
        }

        if (CollectionUtils.isEmpty(typeDefinition.getFields())) {
            typeDefinition.setFields(fields);
        } else {
            typeDefinition.getFields().addAll(fields);
        }

        if (typeDefinition instanceof DomainTypeDefinition) {
            performUniquesAndIndexesOfDomainType(
                    (DomainTypeDefinition) typeDefinition,
                    (Class<? extends TraitType>) javaType, columnNameFieldNameMapping);
        }
    }

    private void performUniquesAndIndexesOfDomainType(
            DomainTypeDefinition domainTypeDefinition, Class<? extends TraitType> javaType,
            Map<String, String> columnNameFieldNameMapping) {
        Table table = javaType.getAnnotation(Table.class);

        Set<UniqueDefinition> uniqueDefinitions = new LinkedHashSet<>();
        Set<IndexDefinition> indexDefinitions = new LinkedHashSet<>();

        if (table != null) {
            for (javax.persistence.Index jpaIndex : table.indexes()) {
                Set<String> columnNames
                        = LiteQL.StringUtils.convertDelimitedParameterToSetOfString(jpaIndex.columnList());

                Set<String> fieldNames = new LinkedHashSet<>();

                for (String columnName : columnNames) {
                    fieldNames.add(getFieldName(columnName, columnNameFieldNameMapping));
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

    private String getFieldName(String columnName, Map<String, String> columnNameFieldNameMapping) {
        String fieldName = columnNameFieldNameMapping.get(columnName);

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

    @Override
    protected void performFieldsOfTrait(
            TraitTypeDefinition traitTypeDefinition, Class<? extends TraitType> traitInterface) {
        Set<Field> fields = new LinkedHashSet<>();

        Set<Method> sortedMethods = getSortedDeclaredMethodsInTraitInterface(traitInterface);

        Method[] columnMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, Column.class, true, true);

        Method[] lobMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, Lob.class, true, true);

        Method[] graphQLFieldMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, GraphQLField.class, true, true);

        Method[] referenceFieldMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, LiteQLReferenceField.class, true, true);

        for (Method method : sortedMethods) {
            if (LiteQL.ClassUtils.isGetMethod(method)) {
                String name = LiteQL.ClassUtils.findFieldNameForMethod(method);

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
        int length = StringField.DEFAULT_LENGTH;

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
            DefaultIdField idField = new DefaultIdField();

            field = idField;
        } else if (fieldType.equals(String.class) && lobAnnotation == null && liteQLReferenceFieldAnnotation == null) {
            DefaultStringField stringField = new DefaultStringField(isGraphQLField);

            stringField.setLength(length);

            field = stringField;
        } else if (fieldType.equals(Long.class) || fieldType.equals(Long.TYPE)) {
            DefaultLongField longField = new DefaultLongField(isGraphQLField);

            field = longField;
        } else if (fieldType.equals(Integer.class) || fieldType.equals(Integer.TYPE)) {
            DefaultIntegerField integerField = new DefaultIntegerField(isGraphQLField);

            field = integerField;
        } else if (Date.class.isAssignableFrom(fieldType)) {
            DefaultTimestampField timestampField = new DefaultTimestampField(isGraphQLField);

            field = timestampField;
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(Boolean.TYPE)) {
            DefaultBooleanField booleanField = new DefaultBooleanField(isGraphQLField);

            field = booleanField;
        } else if (fieldType.equals(BigDecimal.class)) {
            DefaultDecimalField decimalField = new DefaultDecimalField(isGraphQLField);

            field = decimalField;
        } else if (fieldType.equals(String.class) && lobAnnotation != null) {
            DefaultClobField clobField = new DefaultClobField(isGraphQLField);

            field = clobField;
        } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
            DefaultBlobField blobField = new DefaultBlobField(isGraphQLField);

            field = blobField;
        } else if (liteQLReferenceFieldAnnotation != null) {
            DefaultReferenceField referenceField
                    = new DefaultReferenceField(isGraphQLField);

            if (StringUtils.isNotBlank(liteQLReferenceFieldAnnotation.name())) {
                referenceField.setName(liteQLReferenceFieldAnnotation.name());
            }

            referenceField.setDomainTypeName(
                    LiteQL.SchemaUtils.getTypeName(liteQLReferenceFieldAnnotation.targetDomainType()));

            if (Collection.class.isAssignableFrom(fieldType)) {
                referenceField.setCollection(true);
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
