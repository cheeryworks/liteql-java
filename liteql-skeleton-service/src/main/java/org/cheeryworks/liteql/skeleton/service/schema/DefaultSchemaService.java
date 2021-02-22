package org.cheeryworks.liteql.skeleton.service.schema;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.graphql.annotation.GraphQLField;
import org.cheeryworks.liteql.skeleton.graphql.annotation.GraphQLType;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TraitType;
import org.cheeryworks.liteql.skeleton.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.VoidTraitType;
import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLDomainType;
import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLDomainTypeIndex;
import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLTraitInstance;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLBlobField;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLClobField;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLDecimalField;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLField;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLFieldPosition;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLIntegerField;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLLongField;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLReferenceField;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLStringField;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLTimestampField;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.field.IdField;
import org.cheeryworks.liteql.skeleton.schema.field.internal.AbstractField;
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
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.cheeryworks.liteql.skeleton.schema.Schema.NAME_OF_MIGRATIONS_DIRECTORY;
import static org.cheeryworks.liteql.skeleton.schema.Schema.NAME_OF_TYPES_DIRECTORY;
import static org.cheeryworks.liteql.skeleton.schema.Schema.SUFFIX_OF_TYPE_DEFINITION;

public class DefaultSchemaService extends AbstractSchemaService {

    public DefaultSchemaService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);

        if (liteQLProperties.isJsonBasedSchemaEnabled()) {
            processJsonBasedSchema();
        }

        Map<String, Set<TypeDefinition>> typeDefinitionWithinSchemas = getTypeDefinitionWithinSchemas();

        typeDefinitionWithinSchemas.putAll(getAdditionalTypeDefinitionWithinSchemas());

        processGraphQLType(typeDefinitionWithinSchemas);

        for (Map.Entry<String, Set<TypeDefinition>> typeDefinitionWithinSchema
                : typeDefinitionWithinSchemas.entrySet()) {
            for (TypeDefinition typeDefinition : typeDefinitionWithinSchema.getValue()) {

                TypeDefinition existTypeDefinition = getType(typeDefinition.getTypeName());

                if (existTypeDefinition != null && !typeDefinition.equals(existTypeDefinition)) {
                    throw new IllegalStateException(
                            "Definition of type [" + typeDefinition.getTypeName() + "]" +
                                    " not matched with it's typed definition, expected " +
                                    LiteQL.JacksonJsonUtils.toJson(typeDefinition) + ", actual " +
                                    LiteQL.JacksonJsonUtils.toJson(existTypeDefinition));
                }

                addType(typeDefinition);
            }
        }
    }

    private void processJsonBasedSchema() {
        Map<String, SchemaMetadata> schemaMetadataSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        try {
            Reflections reflections = new Reflections(
                    LiteQL.Constants.SCHEMA_DEFINITION_CLASSPATH_ROOT, new ResourcesScanner());

            Set<String> schemaRootPaths = reflections.getResources(Pattern.compile(".*\\.yml"));

            Map<String, String> schemaPathMapping = new HashMap<>();

            for (String schemaRootPath : schemaRootPaths) {
                String schemaName = schemaRootPath
                        .substring(schemaRootPath.lastIndexOf("/") + 1, schemaRootPath.lastIndexOf("."));

                if (schemaMetadataSet.get(schemaName) == null) {
                    schemaMetadataSet.put(schemaName, new SchemaMetadata(schemaName));
                    schemaPathMapping.put(schemaName, schemaRootPath.substring(0, schemaRootPath.lastIndexOf(".")));
                } else {
                    throw new IllegalArgumentException("Schema [" + schemaName + "]"
                            + " exist in path [" + schemaPathMapping.get(schemaName) + "]"
                            + ", but find in another path [" + schemaRootPath + "]");
                }
            }

            Set<String> schemaDefinitionResourcePaths
                    = reflections.getResources(Pattern.compile(".*\\.json"));

            for (String schemaDefinitionResourcePath : schemaDefinitionResourcePaths) {
                for (Map.Entry<String, String> schemaPathEntry : schemaPathMapping.entrySet()) {
                    if (schemaDefinitionResourcePath.startsWith(
                            schemaPathEntry.getValue() + "/" + NAME_OF_TYPES_DIRECTORY)) {
                        String schemaDefinitionResourceRelativePath
                                = schemaDefinitionResourcePath.substring(schemaPathEntry.getValue().length() + 1);

                        if (!schemaDefinitionResourceRelativePath.contains(NAME_OF_TYPES_DIRECTORY)) {
                            continue;
                        }

                        SchemaMetadata schemaMetadata = schemaMetadataSet.get(schemaPathEntry.getKey());

                        String typeName = schemaDefinitionResourceRelativePath.split("/")[1];

                        TypeMetadata typeMetadata = schemaMetadata.getTypeMetadataSet().get(typeName);

                        if (typeMetadata == null) {
                            typeMetadata = new TypeMetadata(typeName);

                            schemaMetadata.getTypeMetadataSet().put(typeName, typeMetadata);
                        }

                        String contentName = schemaDefinitionResourceRelativePath.substring(
                                schemaDefinitionResourceRelativePath.lastIndexOf("/") + 1,
                                schemaDefinitionResourceRelativePath.lastIndexOf("."));

                        String content = IOUtils.toString(
                                getClass().getClassLoader().getResourceAsStream(schemaDefinitionResourcePath),
                                StandardCharsets.UTF_8);

                        if (schemaDefinitionResourceRelativePath.endsWith(SUFFIX_OF_TYPE_DEFINITION)) {
                            typeMetadata.getContents().put(contentName, content);
                        }

                        if (schemaDefinitionResourceRelativePath.contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                            typeMetadata.getMigrationContents().put(contentName, content);
                        }

                        break;
                    }
                }
            }

        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        for (SchemaMetadata schemaMetadata : schemaMetadataSet.values()) {
            processSchemaMetadata(schemaMetadata);
        }
    }

    private Map<String, Set<TypeDefinition>> getTypeDefinitionWithinSchemas() {
        Map<String, Set<TypeDefinition>> typeDefinitionWithinSchemas = new HashMap<>();

        Set<Class<? extends TraitType>> traitTypes = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            Reflections reflections = new Reflections(packageToScan);

            traitTypes.addAll(reflections.getSubTypesOf(TraitType.class));
        }

        for (Class<? extends TraitType> traitType : traitTypes) {
            if (traitType.equals(TraitType.class) || !traitType.isInterface()) {
                continue;
            }

            TypeName typeName = LiteQL.SchemaUtils.getTypeName(traitType);

            if (typeName != null) {
                Set<TypeDefinition> typeDefinitionWithinSchema =
                        typeDefinitionWithinSchemas.get(typeName.getSchema());

                if (typeDefinitionWithinSchema == null) {
                    typeDefinitionWithinSchema = new LinkedHashSet<>();
                    typeDefinitionWithinSchemas.put(typeName.getSchema(), typeDefinitionWithinSchema);
                }

                TraitTypeDefinition traitTypeDefinition =
                        traitInterfaceToTraitTypeDefinition(traitType, typeName);

                typeDefinitionWithinSchema.add(traitTypeDefinition);
            }
        }

        Set<Class<?>> liteQLJavaTypes = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            Reflections reflections = new Reflections(packageToScan);

            liteQLJavaTypes.addAll(reflections.getTypesAnnotatedWith(LiteQLDomainType.class));
        }

        javaTypesToDomainTypeDefinitions(liteQLJavaTypes, typeDefinitionWithinSchemas);

        return typeDefinitionWithinSchemas;
    }

    protected Map<String, Set<TypeDefinition>> getAdditionalTypeDefinitionWithinSchemas() {
        return Collections.EMPTY_MAP;
    }

    protected final void javaTypesToDomainTypeDefinitions(
            Set<Class<?>> liteQLJavaTypes, Map<String, Set<TypeDefinition>> typeDefinitionWithinSchemas) {
        for (Class<?> liteQLJavaType : liteQLJavaTypes) {
            if (!TraitType.class.isAssignableFrom(liteQLJavaType)) {
                continue;
            }

            Class<? extends TraitType> traitType = (Class<? extends TraitType>) liteQLJavaType;

            TypeName typeName = LiteQL.SchemaUtils.getTypeName(traitType);

            if (typeName != null) {
                Set<TypeDefinition> typeDefinitionWithinSchema = typeDefinitionWithinSchemas.get(typeName.getSchema());

                if (typeDefinitionWithinSchema == null) {
                    typeDefinitionWithinSchema = new LinkedHashSet<>();
                    typeDefinitionWithinSchemas.put(typeName.getSchema(), typeDefinitionWithinSchema);
                }

                DomainTypeDefinition domainTypeDefinition = javaTypeToDomainTypeDefinition(traitType, typeName);

                typeDefinitionWithinSchema.add(domainTypeDefinition);
            }
        }
    }

    private void processGraphQLType(Map<String, Set<TypeDefinition>> typeDefinitionWithinSchemas) {
        Set<Class<?>> graphQLJavaTypes = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            Reflections reflections = new Reflections(packageToScan);

            graphQLJavaTypes.addAll(reflections.getTypesAnnotatedWith(GraphQLType.class));
        }

        for (Class<?> graphQLJavaType : graphQLJavaTypes) {
            GraphQLType graphQLType = graphQLJavaType.getAnnotation(GraphQLType.class);

            if (graphQLType != null && !graphQLType.extension().equals(Void.class) && !graphQLType.ignored()) {
                TypeName domainTypeName = LiteQL.SchemaUtils.getTypeName(graphQLType.extension());

                Set<TypeDefinition> domainTypeDefinitions = typeDefinitionWithinSchemas.get(domainTypeName.getSchema());

                for (TypeDefinition domainTypeDefinition : domainTypeDefinitions) {
                    if (domainTypeDefinition.getTypeName().equals(domainTypeName)) {
                        performFieldsOfDomainType((DomainTypeDefinition) domainTypeDefinition, graphQLJavaType);
                        break;
                    }
                }
            }
        }
    }

    private DomainTypeDefinition javaTypeToDomainTypeDefinition(
            Class<? extends TraitType> traitType, TypeName typeName) {
        DomainTypeDefinition domainTypeDefinition = new DomainTypeDefinition(typeName);

        GraphQLType graphQLType = traitType.getAnnotation(GraphQLType.class);

        if (graphQLType != null && graphQLType.ignored()) {
            domainTypeDefinition.setGraphQLType(false);
        }

        LiteQLTraitInstance traitInstance = traitType.getAnnotation(LiteQLTraitInstance.class);

        if (traitInstance != null) {
            domainTypeDefinition.setImplementTrait(LiteQL.SchemaUtils.getTypeName(traitInstance.implement()));
        }

        domainTypeDefinition.setVersion(LiteQL.SchemaUtils.getVersionOfTrait(traitType));

        performFieldsOfDomainType(domainTypeDefinition, traitType);

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

    protected void performFieldsOfDomainType(DomainTypeDefinition domainTypeDefinition, Class<?> javaType) {
        Set<Field> fields = new LinkedHashSet<>();

        List<java.lang.reflect.Field> javaFields = FieldUtils.getAllFieldsList(javaType);

        Method[] liteQLFieldMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, LiteQLField.class, true, true);

        Method[] graphQLFieldMethods
                = MethodUtils.getMethodsWithAnnotation(javaType, GraphQLField.class, true, true);

        for (java.lang.reflect.Field javaField : javaFields) {
            if (Modifier.isFinal(javaField.getModifiers())
                    || Modifier.isStatic(javaField.getModifiers())
                    || Modifier.isTransient(javaField.getModifiers())) {
                continue;
            }

            String javaFieldName = javaField.getName();

            Annotation liteQLFieldAnnotation = getFieldAnnotation(
                    liteQLFieldMethods, javaFieldName, javaField);

            GraphQLField graphQLFieldAnnotation
                    = getAnnotation(graphQLFieldMethods, javaFieldName, javaField, GraphQLField.class);

            Field field = getField(
                    javaType, javaFieldName, javaField.getType(), liteQLFieldAnnotation, graphQLFieldAnnotation);

            fields.add(field);
        }

        if (CollectionUtils.isEmpty(domainTypeDefinition.getFields())) {
            domainTypeDefinition.setFields(fields);
        } else {
            domainTypeDefinition.getFields().addAll(fields);
        }

        if (TraitType.class.isAssignableFrom(javaType)) {
            performUniquesAndIndexesOfDomainType(
                    domainTypeDefinition, (Class<? extends TraitType>) javaType);
        }
    }

    protected void performUniquesAndIndexesOfDomainType(
            DomainTypeDefinition domainTypeDefinition, Class<? extends TraitType> javaType) {
        LiteQLDomainType liteQLDomainType = javaType.getAnnotation(LiteQLDomainType.class);

        Set<UniqueDefinition> uniqueDefinitions = new LinkedHashSet<>();
        Set<IndexDefinition> indexDefinitions = new LinkedHashSet<>();

        if (liteQLDomainType != null) {
            for (LiteQLDomainTypeIndex liteQLDomainTypeIndex : liteQLDomainType.indexes()) {
                Set<String> fieldNames = Arrays.stream(liteQLDomainTypeIndex.fields()).collect(Collectors.toSet());

                if (liteQLDomainTypeIndex.unique()) {
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

    protected void performFieldsOfTrait(
            TraitTypeDefinition traitTypeDefinition, Class<? extends TraitType> traitInterface) {
        Set<Field> fields = new LinkedHashSet<>();

        Set<Method> sortedMethods = getSortedDeclaredMethodsInTraitInterface(traitInterface);

        Method[] liteQLFieldMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, LiteQLField.class, true, true);

        Method[] graphQLFieldMethods
                = MethodUtils.getMethodsWithAnnotation(traitInterface, GraphQLField.class, true, true);

        for (Method method : sortedMethods) {
            if (LiteQL.ClassUtils.isGetMethod(method)) {
                String name = LiteQL.ClassUtils.findFieldNameForMethod(method);

                Annotation liteQLFieldAnnotation
                        = getFieldAnnotation(liteQLFieldMethods, name, method);

                GraphQLField graphQLFieldAnnotation
                        = getAnnotation(graphQLFieldMethods, name, method, GraphQLField.class);

                Field field = getField(
                        traitInterface, name, method.getReturnType(), liteQLFieldAnnotation, graphQLFieldAnnotation);

                fields.add(field);
            }
        }

        if (CollectionUtils.isEmpty(traitTypeDefinition.getFields())) {
            traitTypeDefinition.setFields(fields);
        } else {
            traitTypeDefinition.getFields().addAll(fields);
        }
    }

    protected final Set<Method> getSortedDeclaredMethodsInTraitInterface(Class<? extends TraitType> traitInterface) {
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

        return sortedMethods;
    }

    private void performTraits(TraitTypeDefinition traitTypeDefinition, Class<? extends TraitType> javaType) {
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

    private Field getField(
            Class<?> javaType, String name, Class<?> fieldType,
            Annotation liteQLFieldAnnotation, GraphQLField graphQLFieldAnnotation) {
        AbstractField field = null;

        Boolean isGraphQLField = (graphQLFieldAnnotation != null && graphQLFieldAnnotation.ignore()) ? false : null;

        if (IdField.ID_FIELD_NAME.equalsIgnoreCase(name)) {
            DefaultIdField idField = new DefaultIdField();

            field = idField;
        } else if (fieldType.equals(String.class)
                && (liteQLFieldAnnotation == null || liteQLFieldAnnotation instanceof LiteQLStringField)) {
            DefaultStringField stringField = new DefaultStringField(isGraphQLField);

            if (liteQLFieldAnnotation != null) {
                LiteQLStringField liteQLStringFieldAnnotation = (LiteQLStringField) liteQLFieldAnnotation;

                stringField.setLength(liteQLStringFieldAnnotation.length());

                if (!liteQLStringFieldAnnotation.nullable()) {
                    stringField.setNullable(false);
                }
            }

            field = stringField;
        } else if (fieldType.equals(Long.class) || fieldType.equals(Long.TYPE)) {
            DefaultLongField longField = new DefaultLongField(isGraphQLField);

            if (liteQLFieldAnnotation != null) {
                LiteQLLongField liteQLLongFieldAnnotation = (LiteQLLongField) liteQLFieldAnnotation;

                if (!liteQLLongFieldAnnotation.nullable()) {
                    longField.setNullable(false);
                }
            }

            field = longField;
        } else if (fieldType.equals(Integer.class) || fieldType.equals(Integer.TYPE)) {
            DefaultIntegerField integerField = new DefaultIntegerField(isGraphQLField);

            if (liteQLFieldAnnotation != null) {
                LiteQLIntegerField liteQLIntegerFieldAnnotation = (LiteQLIntegerField) liteQLFieldAnnotation;

                if (!liteQLIntegerFieldAnnotation.nullable()) {
                    integerField.setNullable(false);
                }
            }

            field = integerField;
        } else if (Date.class.isAssignableFrom(fieldType)) {
            DefaultTimestampField timestampField = new DefaultTimestampField(isGraphQLField);

            if (liteQLFieldAnnotation != null) {
                LiteQLTimestampField liteQLTimestampFieldAnnotation = (LiteQLTimestampField) liteQLFieldAnnotation;

                if (!liteQLTimestampFieldAnnotation.nullable()) {
                    timestampField.setNullable(false);
                }
            }

            field = timestampField;
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(Boolean.TYPE)) {
            DefaultBooleanField booleanField = new DefaultBooleanField(isGraphQLField);

            field = booleanField;
        } else if (fieldType.equals(BigDecimal.class)) {
            DefaultDecimalField decimalField = new DefaultDecimalField(isGraphQLField);

            if (liteQLFieldAnnotation != null) {
                LiteQLDecimalField liteQLDecimalFieldAnnotation = (LiteQLDecimalField) liteQLFieldAnnotation;

                decimalField.setPrecision(liteQLDecimalFieldAnnotation.precision());
                decimalField.setScale(liteQLDecimalFieldAnnotation.scale());

                if (!liteQLDecimalFieldAnnotation.nullable()) {
                    decimalField.setNullable(false);
                }
            }

            field = decimalField;
        } else if (fieldType.equals(String.class)
                && liteQLFieldAnnotation != null && liteQLFieldAnnotation instanceof LiteQLClobField) {
            DefaultClobField clobField = new DefaultClobField(isGraphQLField);

            if (liteQLFieldAnnotation != null) {
                LiteQLClobField liteQLClobFieldAnnotation = (LiteQLClobField) liteQLFieldAnnotation;

                if (!liteQLClobFieldAnnotation.nullable()) {
                    clobField.setNullable(false);
                }
            }

            field = clobField;
        } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
            DefaultBlobField blobField = new DefaultBlobField(isGraphQLField);

            if (liteQLFieldAnnotation != null) {
                LiteQLBlobField liteQLBlobFieldAnnotation = (LiteQLBlobField) liteQLFieldAnnotation;

                if (!liteQLBlobFieldAnnotation.nullable()) {
                    blobField.setNullable(false);
                }
            }

            field = blobField;
        } else if (liteQLFieldAnnotation != null && liteQLFieldAnnotation instanceof LiteQLReferenceField) {
            DefaultReferenceField referenceField
                    = new DefaultReferenceField(isGraphQLField);

            LiteQLReferenceField liteQLReferenceFieldAnnotation = (LiteQLReferenceField) liteQLFieldAnnotation;

            if (!liteQLReferenceFieldAnnotation.nullable()) {
                referenceField.setNullable(false);
            }

            if (StringUtils.isNotBlank(liteQLReferenceFieldAnnotation.name())) {
                referenceField.setName(liteQLReferenceFieldAnnotation.name());
            }

            referenceField.setDomainTypeName(
                    LiteQL.SchemaUtils.getTypeName(liteQLReferenceFieldAnnotation.targetDomainType()));

            if (Collection.class.isAssignableFrom(fieldType)) {
                referenceField.setCollection(true);

                if (!liteQLReferenceFieldAnnotation.mappedDomainType().equals(VoidTraitType.class)
                        && !liteQLReferenceFieldAnnotation.targetDomainType().equals(
                        liteQLReferenceFieldAnnotation.mappedDomainType())) {
                    referenceField.setMappedDomainTypeName(
                            LiteQL.SchemaUtils.getTypeName(liteQLReferenceFieldAnnotation.targetDomainType()));
                }
            }

            field = referenceField;
        }

        if (field != null) {
            if (StringUtils.isBlank(field.getName())) {
                field.setName(name);
            }
        }

        if (field == null) {
            throw new IllegalArgumentException("Can not parse field [" + name + "] of [" + javaType.getName() + "]");
        }

        return field;
    }

    private static Annotation getFieldAnnotation(
            Method[] methods, String fieldName, AccessibleObject accessibleObject) {
        Annotation[] annotations = accessibleObject.getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getAnnotation(LiteQLField.class) != null) {
                return annotation;
            }
        }

        for (Method method : methods) {
            if (LiteQL.ClassUtils.findFieldNameForMethod(method).equalsIgnoreCase(fieldName)) {
                annotations = method.getAnnotations();

                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getAnnotation(LiteQLField.class) != null) {
                        return annotation;
                    }
                }
            }
        }

        return null;
    }

    protected <T extends Annotation> T getAnnotation(
            Method[] methods, String fieldName, AccessibleObject accessibleObject, Class<T> annotationClass) {
        T annotation = accessibleObject.getAnnotation(annotationClass);

        if (annotation == null) {
            for (Method method : methods) {
                if (LiteQL.ClassUtils.findFieldNameForMethod(method).equalsIgnoreCase(fieldName)) {
                    annotation = method.getAnnotation(annotationClass);
                    break;
                }
            }
        }

        return annotation;
    }

}
