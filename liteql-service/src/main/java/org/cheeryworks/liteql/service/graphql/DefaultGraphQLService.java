package org.cheeryworks.liteql.service.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.Type;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.cheeryworks.liteql.model.annotation.ReferenceField;
import org.cheeryworks.liteql.model.annotation.graphql.GraphQLEntity;
import org.cheeryworks.liteql.model.annotation.graphql.GraphQLField;
import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.graphql.Scalars;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.Trait;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.util.ClassUtil;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.model.util.StringUtil;
import org.cheeryworks.liteql.model.util.graphql.GraphQLConstants;
import org.cheeryworks.liteql.service.GraphQLService;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.util.GraphQLServiceUtil;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StreamUtils;

import javax.persistence.Column;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static org.cheeryworks.liteql.util.GraphQLServiceUtil.GRAPHQL_NAME_CONCAT;

public class DefaultGraphQLService implements GraphQLService {

    private static final String EMPTY_SCHEMA;

    private Repository repository;

    private ObjectMapper objectMapper;

    private QueryService queryService;

    private boolean liteQLBasedGraphQLSchemaEnabled;

    private boolean annotationBasedGraphQLSchemaEnabled;

    private Scalars scalars;

    private GraphQL graphQL;

    static {
        StringBuilder emptySchemaBuilder = new StringBuilder();

        emptySchemaBuilder
                .append("schema {\n")
                .append("    query: ").append(GraphQLConstants.QUERY_TYPE_NAME).append("\n")
                .append("    mutation: ").append(GraphQLConstants.MUTATION_TYPE_NAME).append("\n")
                .append("}\n")
                .append("\n")
                .append("type Query {\n")
                .append("}\n")
                .append("\n")
                .append("type Mutation {\n")
                .append("}\n")
                .append("\n");

        EMPTY_SCHEMA = emptySchemaBuilder.toString();
    }

    public DefaultGraphQLService(
            Repository repository, ObjectMapper objectMapper, QueryService queryService,
            boolean liteQLBasedGraphQLSchemaEnabled, boolean annotationBasedGraphQLSchemaEnabled) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.queryService = queryService;
        this.liteQLBasedGraphQLSchemaEnabled = liteQLBasedGraphQLSchemaEnabled;
        this.annotationBasedGraphQLSchemaEnabled = annotationBasedGraphQLSchemaEnabled;
        this.scalars = new Scalars(objectMapper);

        try {
            GraphQLSchema graphQLSchema = buildingSchema();

            this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        } catch (Exception ex) {
            throw new IllegalStateException("GraphQL initializing failed", ex);
        }
    }

    private GraphQLSchema buildingSchema() {
        try {
            SchemaParser schemaParser = new SchemaParser();
            TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(EMPTY_SCHEMA);

            PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
                    = new PathMatchingResourcePatternResolver();

            Resource[] graphQLSchemaResources = pathMatchingResourcePatternResolver
                    .getResources("classpath*:graphql/*.graphqls");

            for (Resource graphQLSchemaResource : graphQLSchemaResources) {
                String graphQLSchemaDefinition = StreamUtils.copyToString(
                        graphQLSchemaResource.getInputStream(), StandardCharsets.UTF_8);
                typeDefinitionRegistry.merge(schemaParser.parse(graphQLSchemaDefinition));
            }

            List<OperationTypeDefinition> operationTypeDefinitions
                    = typeDefinitionRegistry.schemaDefinition().get().getOperationTypeDefinitions();

            Set<String> operationTypeNames = new HashSet<>();

            Map<Class, Map<String, String>> graphQLFieldReferencesWithDomainType = new HashMap<>();

            for (OperationTypeDefinition operationTypeDefinition : operationTypeDefinitions) {
                operationTypeNames.add(operationTypeDefinition.getTypeName().getName());
            }

            if (liteQLBasedGraphQLSchemaEnabled) {
                processLiteQLBasedGraphQLSchema(typeDefinitionRegistry, graphQLFieldReferencesWithDomainType);
            }

            if (annotationBasedGraphQLSchemaEnabled) {
                processAnnotationBasedGraphQLSchema(typeDefinitionRegistry, graphQLFieldReferencesWithDomainType);
            }

            generateDefaultQueries(schemaParser, typeDefinitionRegistry, operationTypeNames);

            generateDefaultInputTypes(schemaParser, typeDefinitionRegistry, operationTypeNames);

            generateDefaultMutations(schemaParser, typeDefinitionRegistry, operationTypeNames);

            RuntimeWiring runtimeWiring = buildWiring(
                    typeDefinitionRegistry.types(), operationTypeNames, graphQLFieldReferencesWithDomainType);

            SchemaGenerator schemaGenerator = new SchemaGenerator();

            return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void processLiteQLBasedGraphQLSchema(
            TypeDefinitionRegistry typeDefinitionRegistry,
            Map<Class, Map<String, String>> graphQLFieldReferencesWithDomainType) {
        Map<String, ObjectTypeDefinition.Builder> objectTypeDefinitions = new HashMap<>();

        for (String schema : repository.getSchemaNames()) {
            for (DomainType domainType : repository.getDomainTypes(schema)) {
                String objectTypeName = domainType
                        .getFullname().replaceAll("\\" + LiteQLConstants.NAME_CONCAT, GRAPHQL_NAME_CONCAT);

                ObjectTypeDefinition.Builder objectTypeDefinitionBuilder = ObjectTypeDefinition
                        .newObjectTypeDefinition()
                        .name(objectTypeName);

                objectTypeDefinitions.put(objectTypeName, objectTypeDefinitionBuilder);

                for (Field field : domainType.getFields()) {
                    if (DataType.Reference.equals(field.getType())) {
                        continue;
                    }
                    
                    FieldDefinition.Builder fieldDefinitionBuilder = FieldDefinition
                            .newFieldDefinition()
                            .name(field.getName())
                            .type(new TypeName(getGraphQLTypeFromDataType(field.getType())))
                            .inputValueDefinitions(defaultFieldArguments());

                    FieldDefinition fieldDefinition = fieldDefinitionBuilder.build();

                    objectTypeDefinitionBuilder.fieldDefinition(fieldDefinition);
                }
            }
        }

        objectTypeDefinitions.values().stream().map(x -> x.build()).forEach(typeDefinitionRegistry::add);
    }

    private void processAnnotationBasedGraphQLSchema(
            TypeDefinitionRegistry typeDefinitionRegistry,
            Map<Class, Map<String, String>> graphQLFieldReferencesWithDomainType) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(GraphQLEntity.class));

        Set<BeanDefinition> graphQLBeanDefinitions = new HashSet<>();

        for (String packageToScan : LiteQLConstants.getPackageToScan()) {
            graphQLBeanDefinitions.addAll(scanner.findCandidateComponents(packageToScan));
        }

        Map<String, ObjectTypeDefinition.Builder> objectTypeDefinitions = new HashMap<>();

        Map<Class, String> objectImplements = new HashMap<>();

        for (BeanDefinition graphQLBeanDefinition : graphQLBeanDefinitions) {
            Class domainType = ClassUtil.getClass(graphQLBeanDefinition.getBeanClassName());

            GraphQLEntity graphQLEntity = AnnotationUtils.findAnnotation(domainType, GraphQLEntity.class);

            if (graphQLEntity.extension().equals(Void.class)) {
                String objectTypeName = GraphQLServiceUtil.getObjectTypeName(domainType);

                ObjectTypeDefinition.Builder objectTypeDefinitionBuilder = ObjectTypeDefinition
                        .newObjectTypeDefinition()
                        .name(objectTypeName);

                if (objectTypeDefinitions.containsKey(objectTypeName)) {
                    throw new IllegalStateException(
                            "Duplicated ObjectType [" + objectTypeName + "] in different package");
                } else {
                    objectTypeDefinitions.put(objectTypeName, objectTypeDefinitionBuilder);

                    if (!graphQLEntity.implement().equals(Void.class)) {
                        if (objectImplements.containsKey(graphQLEntity.implement())) {
                            throw new IllegalStateException(
                                    "Duplicated implements of"
                                            + " [" + graphQLEntity.implement().getName() + "]"
                                            + " in different package");
                        } else {
                            objectImplements.put(
                                    graphQLEntity.implement(), objectTypeName);
                        }
                    }
                }
            }
        }

        for (BeanDefinition graphQLBeanDefinition : graphQLBeanDefinitions) {
            Class domainType = ClassUtil.getClass(graphQLBeanDefinition.getBeanClassName());

            GraphQLEntity graphQLEntity
                    = AnnotationUtils.findAnnotation(domainType, GraphQLEntity.class);

            String objectTypeName = graphQLEntity.extension().equals(Void.class)
                    ? GraphQLServiceUtil.getObjectTypeName(domainType)
                    : GraphQLServiceUtil.getObjectTypeName(graphQLEntity.extension());

            Map<String, String> graphQLFieldReferences = new HashMap<>();

            getFieldDefinitions(domainType, objectImplements).stream().forEach((fieldDefinition) -> {
                objectTypeDefinitions
                        .get(objectTypeName)
                        .fieldDefinition(fieldDefinition);

                String fieldName = getFieldName(domainType, fieldDefinition.getName());

                if (StringUtils.isNotBlank(fieldName)) {
                    graphQLFieldReferences.put(fieldDefinition.getName(), fieldName);
                }
            });

            if (MapUtils.isNotEmpty(graphQLFieldReferences)) {
                graphQLFieldReferencesWithDomainType.put(domainType, graphQLFieldReferences);
            }
        }

        objectTypeDefinitions.values().stream().map(x -> x.build()).forEach(typeDefinitionRegistry::add);
    }

    private List<FieldDefinition> getFieldDefinitions(Class graphQLType, Map<Class, String> objectImplements) {
        Map<String, FieldDefinition> fieldDefinitions = new HashMap<>();

        List<java.lang.reflect.Field> fields = FieldUtils.getAllFieldsList(graphQLType);

        for (java.lang.reflect.Field field : fields) {
            if (field.getModifiers() != Modifier.PRIVATE) {
                continue;
            }

            String fieldName = field.getName();

            GraphQLField graphQLField = field.getAnnotation(GraphQLField.class);
            ReferenceField referenceField = field.getAnnotation(ReferenceField.class);

            if (graphQLField != null) {
                if (graphQLField.ignore()) {
                    continue;
                }

                if (StringUtils.isNotBlank(graphQLField.name())) {
                    fieldName = graphQLField.name();
                }
            }

            Type fieldType = getGraphQLType(graphQLType, field, graphQLField, referenceField, objectImplements);

            FieldDefinition.Builder fieldDefinitionBuilder = FieldDefinition
                    .newFieldDefinition()
                    .name(fieldName)
                    .type(fieldType)
                    .inputValueDefinitions(defaultFieldArguments());

            FieldDefinition fieldDefinition = fieldDefinitionBuilder.build();

            fieldDefinitions.put(fieldDefinition.getName(), fieldDefinition);
        }

        Method[] methods = MethodUtils.getMethodsWithAnnotation(graphQLType, GraphQLField.class, true, false);

        for (Method method : methods) {
            GraphQLField graphQLField = method.getAnnotation(GraphQLField.class);
            ReferenceField referenceField = method.getAnnotation(ReferenceField.class);

            String fieldName = BeanUtils.findPropertyForMethod(method).getName();

            fieldName = StringUtils.uncapitalize(fieldName);

            if (fieldDefinitions.containsKey(fieldName)) {
                fieldDefinitions.remove(fieldName);
            }

            if (graphQLField.ignore()) {
                continue;
            } else {
                Type fieldType = getGraphQLType(graphQLType, method, graphQLField, referenceField, objectImplements);

                FieldDefinition.Builder fieldDefinitionBuilder = FieldDefinition
                        .newFieldDefinition()
                        .name(graphQLField.name())
                        .type(fieldType)
                        .inputValueDefinitions(defaultFieldArguments());

                FieldDefinition fieldDefinition = fieldDefinitionBuilder.build();

                fieldDefinitions.put(fieldDefinition.getName(), fieldDefinition);
            }
        }

        return new ArrayList<>(fieldDefinitions.values());
    }

    private Type getGraphQLType(
            Class type, Member member, GraphQLField graphQLField, ReferenceField referenceField,
            Map<Class, String> objectImplements) {
        if (graphQLField != null && graphQLField.reference()) {
            if (objectImplements.containsKey(referenceField.targetDomainType())) {
                return new TypeName(objectImplements.get(referenceField.targetDomainType()));
            }

            return new TypeName(GraphQLServiceUtil.getObjectTypeName(referenceField.targetDomainType()));
        }

        if (member instanceof java.lang.reflect.Field) {
            return getGraphQLTypeFromField(type, (java.lang.reflect.Field) member);
        } else if (member instanceof Method) {
            return getGraphQLTypeFromMethod(type, (Method) member);
        }

        throw new IllegalStateException("Type not recognized");
    }

    private Type getGraphQLTypeFromField(Class type, java.lang.reflect.Field field) {
        Column column = field.getAnnotation(Column.class);

        if (field.getName().equals(IdField.ID_FIELD_NAME)) {
            return new NonNullType(new TypeName(graphql.Scalars.GraphQLID.getName()));
        } else if (field.getType().isPrimitive()) {
            if (column != null && !column.nullable()) {
                return new NonNullType(new TypeName(getGraphQLTypeFromPrimitiveType(field.getType())));
            }
        } else if (Collection.class.isAssignableFrom(field.getType())) {
            java.lang.reflect.Type typeArgument
                    = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            if (typeArgument instanceof Class) {
                return new ListType(
                        new NonNullType(
                                new TypeName(
                                        GraphQLServiceUtil.getObjectTypeName((Class) typeArgument))));
            } else {
                String typeName = GraphQLServiceUtil.getObjectTypeName(
                        ((Class) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0]));
                return new ListType(new NonNullType(new TypeName(typeName)));
            }
        }

        return new TypeName(getGraphQLTypeFromPrimitiveType(field.getType()));
    }

    private Type getGraphQLTypeFromMethod(Class type, Method method) {
        Column column = method.getAnnotation(Column.class);

        if (method.getReturnType().isPrimitive()) {
            if (column != null && !column.nullable()) {
                return new NonNullType(new TypeName(getGraphQLTypeFromPrimitiveType(method.getReturnType())));
            }
        } else if (Collection.class.isAssignableFrom(method.getReturnType())) {
            java.lang.reflect.Type typeArgument
                    = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];

            if (typeArgument instanceof Class) {
                return new ListType(
                        new NonNullType(
                                new TypeName(
                                        GraphQLServiceUtil.getObjectTypeName((Class) typeArgument))));
            } else {
                String typeName = GraphQLServiceUtil.getObjectTypeName(
                        ((Class) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0]));
                return new ListType(new NonNullType(new TypeName(typeName)));
            }
        }

        return new TypeName(getGraphQLTypeFromPrimitiveType(method.getReturnType()));
    }

    private String getGraphQLTypeFromDataType(DataType dataType) {
        switch (dataType) {
            case Id:
            case String:
                return graphql.Scalars.GraphQLString.getName();
            case Integer:
                return graphql.Scalars.GraphQLInt.getName();
            case Timestamp:
                return this.scalars.getScalarDate().getName();
            case Boolean:
                return graphql.Scalars.GraphQLBoolean.getName();
            case Decimal:
                return this.scalars.getScalarBigDecimal().getName();
            case Clob:
                return graphql.Scalars.GraphQLString.getName();
            case Blob:
                return graphql.Scalars.GraphQLByte.getName();
            default:
                throw new IllegalArgumentException("Unsupported field type: " + dataType.name());
        }
    }

    private String getGraphQLTypeFromPrimitiveType(Class type) {
        if (String.class.isAssignableFrom(type)) {
            return graphql.Scalars.GraphQLString.getName();
        } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return this.scalars.getScalarLong().getName();
        } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return graphql.Scalars.GraphQLInt.getName();
        } else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return graphql.Scalars.GraphQLBoolean.getName();
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return this.scalars.getScalarBigDecimal().getName();
        } else if (Date.class.isAssignableFrom(type)) {
            return this.scalars.getScalarDate().getName();
        }

        throw new IllegalArgumentException(type.getName() + " not supported");
    }

    private String getFieldName(Class domainJavaType, String graphQLFieldName) {
        DomainType domainType = null;

        try {
            repository.getDomainType(Trait.getTypeName(domainJavaType));
        } catch (Exception ex) {
        }

        if (domainType == null) {
            return null;
        }

        Set<Field> fields = domainType.getFields();

        for (Field field : fields) {
            if (field.isGraphQLField() && graphQLFieldName.equals(field.getName())) {
                return field.getName();
            }
        }

        Method[] methods = MethodUtils.getMethodsWithAnnotation(domainJavaType, GraphQLField.class, true, false);

        for (Method method : methods) {
            GraphQLField graphQLField = method.getAnnotation(GraphQLField.class);

            if (graphQLField != null && graphQLFieldName.equals(graphQLField.name())) {
                String domainFieldName = BeanUtils.findPropertyForMethod(method).getName();

                return StringUtils.uncapitalize(domainFieldName);
            }
        }

        return null;
    }

    private List<InputValueDefinition> defaultFieldArguments() {
        List<InputValueDefinition> arguments = new ArrayList<>();

        arguments.add(
                InputValueDefinition.newInputValueDefinition()
                        .name(GraphQLConstants.QUERY_ARGUMENT_NAME_CONDITIONS)
                        .type(new ListType(
                                new NonNullType(
                                        new TypeName(GraphQLConstants.INPUT_TYPE_CONDITION_NAME))))
                        .build());

        arguments.add(
                InputValueDefinition.newInputValueDefinition()
                        .name(GraphQLConstants.QUERY_ARGUMENT_NAME_ORDER_BY)
                        .type(new ListType(
                                new NonNullType(
                                        new TypeName(GraphQLConstants.INPUT_TYPE_SORT_NAME))))
                        .build());

        return arguments;
    }

    private void generateDefaultInputTypes(
            SchemaParser schemaParser, TypeDefinitionRegistry typeDefinitionRegistry, Set<String> operationTypeNames) {
        for (Map.Entry<String, TypeDefinition> typeEntry : typeDefinitionRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition
                    && !operationTypeNames.contains(typeEntry.getKey())) {
                StringBuilder inputTypeDefinitionBuilder = new StringBuilder();

                inputTypeDefinitionBuilder.append("input ").append(typeEntry.getKey()).append("Input").append(" {");

                processInputTypeFields(
                        inputTypeDefinitionBuilder,
                        typeDefinitionRegistry.types(),
                        ((ObjectTypeDefinition) typeEntry.getValue()).getFieldDefinitions());

                inputTypeDefinitionBuilder
                        .append("}\n")
                        .append("\n");

                typeDefinitionRegistry.merge(schemaParser.parse(inputTypeDefinitionBuilder.toString()));
            }
        }
    }

    private void processInputTypeFields(
            StringBuilder inputTypeDefinitionBuilder,
            Map<String, TypeDefinition> typeDefinitions, List<FieldDefinition> fieldDefinitions) {
        for (int i = 0; i < fieldDefinitions.size(); i++) {
            FieldDefinition fieldDefinition = fieldDefinitions.get(i);

            if (fieldDefinition.getType() instanceof TypeName) {
                TypeName typeName = (TypeName) fieldDefinition.getType();

                inputTypeDefinitionBuilder
                        .append("    ")
                        .append(fieldDefinition.getName())
                        .append(": ")
                        .append(typeName.getName());

                if (typeDefinitions.containsKey(typeName.getName())) {
                    inputTypeDefinitionBuilder.append("Input");
                }

                inputTypeDefinitionBuilder.append("\n");
            } else if (fieldDefinition.getType() instanceof NonNullType) {
                if (((NonNullType) fieldDefinition.getType()).getType() instanceof TypeName) {
                    TypeName typeName = (TypeName) ((NonNullType) fieldDefinition.getType()).getType();

                    if (!typeName.getName().equals(graphql.Scalars.GraphQLID.getName())) {
                        inputTypeDefinitionBuilder
                                .append("    ")
                                .append(fieldDefinition.getName())
                                .append(": ")
                                .append(typeName.getName());

                        if (typeDefinitions.containsKey(typeName.getName())) {
                            inputTypeDefinitionBuilder.append("Input");
                        }

                        inputTypeDefinitionBuilder
                                .append("!")
                                .append("\n");
                    }
                } else if (((NonNullType) fieldDefinition.getType()).getType() instanceof ListType) {
                    ListType listType = (ListType) ((NonNullType) fieldDefinition.getType()).getType();

                    processListTypeInInputType(
                            inputTypeDefinitionBuilder, typeDefinitions, fieldDefinition, listType);
                }
            } else if (fieldDefinition.getType() instanceof ListType) {
                ListType listType = (ListType) fieldDefinition.getType();

                processListTypeInInputType(
                        inputTypeDefinitionBuilder, typeDefinitions, fieldDefinition, listType);
            }
        }
    }

    private void processListTypeInInputType(
            StringBuilder inputTypeDefinitionBuilder,
            Map<String, TypeDefinition> typeDefinitions,
            FieldDefinition fieldDefinition, ListType listType) {
        TypeName typeName;

        if (listType.getType() instanceof NonNullType) {
            typeName = (TypeName) ((NonNullType) listType.getType()).getType();
        } else {
            typeName = (TypeName) listType.getType();
        }

        inputTypeDefinitionBuilder
                .append("    ")
                .append(fieldDefinition.getName())
                .append(": ")
                .append("[")
                .append(typeName.getName());

        if (typeDefinitions.containsKey(typeName.getName())) {
            inputTypeDefinitionBuilder.append("Input");
        }

        inputTypeDefinitionBuilder
                .append("]")
                .append("\n");
    }

    private void generateDefaultQueries(
            SchemaParser schemaParser, TypeDefinitionRegistry typeDefinitionRegistry, Set<String> operationTypeNames) {
        for (Map.Entry<String, TypeDefinition> typeEntry : typeDefinitionRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition
                    && !operationTypeNames.contains(typeEntry.getKey())) {
                String queryName = StringUtils.uncapitalize(typeEntry.getKey());

                StringBuilder queryDefinitionBuilder = new StringBuilder();

                queryDefinitionBuilder
                        .append("extend type ").append(GraphQLConstants.QUERY_TYPE_NAME).append(" {\n");

                queryDefinitionBuilder
                        .append("  ")
                        .append(queryName)
                        .append("(id: " + graphql.Scalars.GraphQLID.getName() + "!): ")
                        .append(typeEntry.getKey()).append("!");

                queryName = StringUtils.uncapitalize(StringUtil.plural(typeEntry.getKey()));

                queryDefinitionBuilder
                        .append("  ").append(queryName).append("(");

                queryDefinitionBuilder
                        .append(GraphQLConstants.QUERY_ARGUMENT_NAME_CONDITIONS)
                        .append(": ").append("[" + GraphQLConstants.INPUT_TYPE_CONDITION_NAME + "!]");

                queryDefinitionBuilder.append(", ");

                queryDefinitionBuilder
                        .append(GraphQLConstants.QUERY_ARGUMENT_NAME_ORDER_BY)
                        .append(": ").append("[" + GraphQLConstants.INPUT_TYPE_SORT_NAME + "!]");

                queryDefinitionBuilder.append(", ");

                queryDefinitionBuilder
                        .append(GraphQLConstants.QUERY_ARGUMENT_NAME_PAGINATION_OFFSET)
                        .append(": ").append("Int");

                queryDefinitionBuilder.append(", ");

                queryDefinitionBuilder
                        .append(GraphQLConstants.QUERY_ARGUMENT_NAME_PAGINATION_FIRST)
                        .append(": ").append("Int");

                queryDefinitionBuilder.append(")");

                queryDefinitionBuilder.append(": [").append(typeEntry.getKey()).append("]\n");

                queryDefinitionBuilder
                        .append("}\n")
                        .append("\n");

                typeDefinitionRegistry.merge(schemaParser.parse(queryDefinitionBuilder.toString()));
            }
        }
    }

    private void generateDefaultMutations(
            SchemaParser schemaParser, TypeDefinitionRegistry typeDefinitionRegistry, Set<String> operationTypeNames) {
        for (Map.Entry<String, TypeDefinition> typeEntry : typeDefinitionRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition
                    && !operationTypeNames.contains(typeEntry.getKey())) {
                StringBuilder mutationDefinitionBuilder = new StringBuilder();

                mutationDefinitionBuilder
                        .append("extend type ").append(GraphQLConstants.MUTATION_TYPE_NAME).append(" {\n");

                mutationDefinitionBuilder
                        .append("  ")
                        .append(GraphQLConstants.MUTATION_NAME_PREFIX_CREATE)
                        .append(typeEntry.getKey())
                        .append("(");

                processMutationInput(
                        mutationDefinitionBuilder,
                        typeDefinitionRegistry.types(),
                        ((ObjectTypeDefinition) typeEntry.getValue()).getFieldDefinitions());

                mutationDefinitionBuilder
                        .append(")");

                mutationDefinitionBuilder
                        .append(": ").append(typeEntry.getKey()).append("\n");

                mutationDefinitionBuilder
                        .append("}\n")
                        .append("\n");

                typeDefinitionRegistry.merge(schemaParser.parse(mutationDefinitionBuilder.toString()));
            }
        }
    }

    private void processMutationInput(
            StringBuilder mutationDefinitionBuilder, Map<String, TypeDefinition> typeDefinitions,
            List<FieldDefinition> fieldDefinitions) {
        for (int i = 0; i < fieldDefinitions.size(); i++) {
            FieldDefinition fieldDefinition = fieldDefinitions.get(i);

            if (fieldDefinition.getType() instanceof TypeName) {
                TypeName typeName = (TypeName) fieldDefinition.getType();

                mutationDefinitionBuilder
                        .append(fieldDefinition.getName());

                if (typeDefinitions.containsKey(typeName.getName())) {
                    mutationDefinitionBuilder
                            .append(GraphQLConstants.MUTATION_INPUT_FIELD_ID_NAME)
                            .append(": ")
                            .append(graphql.Scalars.GraphQLID.getName());
                } else {
                    mutationDefinitionBuilder.append(": ")
                            .append(typeName.getName());
                }
            } else if (fieldDefinition.getType() instanceof NonNullType) {
                if (((NonNullType) fieldDefinition.getType()).getType() instanceof TypeName) {
                    TypeName typeName = (TypeName) ((NonNullType) fieldDefinition.getType()).getType();

                    if (!typeName.getName().equals(graphql.Scalars.GraphQLID.getName())) {
                        mutationDefinitionBuilder
                                .append(fieldDefinition.getName());

                        if (typeDefinitions.containsKey(typeName.getName())) {
                            mutationDefinitionBuilder
                                    .append(GraphQLConstants.MUTATION_INPUT_FIELD_ID_NAME)
                                    .append(": ")
                                    .append(graphql.Scalars.GraphQLID.getName());
                        } else {
                            mutationDefinitionBuilder.append(": ")
                                    .append(typeName.getName());
                        }

                        mutationDefinitionBuilder.append("!");
                    }
                }
            }

            if (i < fieldDefinitions.size() - 1) {
                mutationDefinitionBuilder.append(", ");
            }
        }
    }

    private RuntimeWiring buildWiring(
            Map<String, TypeDefinition> typeDefinitions, Set<String> operationTypeNames,
            Map<Class, Map<String, String>> graphQLFieldReferencesWithDomainType) {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();

        builder.scalar(this.scalars.getScalarLong());
        builder.scalar(this.scalars.getScalarBigDecimal());
        builder.scalar(this.scalars.getScalarValue());
        builder.scalar(this.scalars.getScalarDate());

        for (Map.Entry<String, TypeDefinition> typeEntry : typeDefinitions.entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition
                    && !operationTypeNames.contains(typeEntry.getKey())) {

                List<FieldDefinition> fieldDefinitions
                        = ((ObjectTypeDefinition) typeEntry.getValue()).getFieldDefinitions();

                Set<String> complexFields = new HashSet<>();

                for (int i = 0; i < fieldDefinitions.size(); i++) {
                    FieldDefinition fieldDefinition = fieldDefinitions.get(i);

                    if (fieldDefinition.getType() instanceof TypeName) {
                        TypeName typeName = (TypeName) fieldDefinition.getType();

                        if (typeDefinitions.containsKey(typeName.getName())) {
                            complexFields.add(fieldDefinition.getName());
                        }
                    } else if (fieldDefinition.getType() instanceof NonNullType) {
                        if (((NonNullType) fieldDefinition.getType()).getType() instanceof TypeName) {
                            TypeName typeName = (TypeName) ((NonNullType) fieldDefinition.getType()).getType();

                            if (!typeName.getName().equals(graphql.Scalars.GraphQLID.getName())) {
                                if (typeDefinitions.containsKey(typeName.getName())) {
                                    complexFields.add(fieldDefinition.getName());
                                }
                            }
                        } else if (((NonNullType) fieldDefinition.getType()).getType() instanceof ListType) {
                            ListType listType = (ListType) ((NonNullType) fieldDefinition.getType()).getType();

                            TypeName typeName;

                            if (listType.getType() instanceof NonNullType) {
                                typeName = (TypeName) ((NonNullType) listType.getType()).getType();
                            } else {
                                typeName = (TypeName) listType.getType();
                            }

                            if (typeDefinitions.containsKey(typeName.getName())) {
                                complexFields.add(fieldDefinition.getName());
                            }
                        }
                    } else if (fieldDefinition.getType() instanceof ListType) {
                        ListType listType = (ListType) fieldDefinition.getType();

                        TypeName typeName;

                        if (listType.getType() instanceof NonNullType) {
                            typeName = (TypeName) ((NonNullType) listType.getType()).getType();
                        } else {
                            typeName = (TypeName) listType.getType();
                        }

                        if (typeDefinitions.containsKey(typeName.getName())) {
                            complexFields.add(fieldDefinition.getName());
                        }
                    }
                }

                if (complexFields.size() > 0) {
                    TypeRuntimeWiring.Builder typeRuntimeWiringBuilder = newTypeWiring(typeEntry.getValue().getName());

                    for (String complexField : complexFields) {
                        typeRuntimeWiringBuilder.dataFetcher(
                                complexField,
                                new GraphQLQueryDataFetcher(
                                        repository, objectMapper, queryService, graphQLFieldReferencesWithDomainType));
                    }

                    builder.type(typeRuntimeWiringBuilder.build());
                }
            }
        }

        builder.type(newTypeWiring(GraphQLConstants.QUERY_TYPE_NAME)
                .defaultDataFetcher(
                        new GraphQLQueryDataFetcher(
                                repository, objectMapper, queryService, graphQLFieldReferencesWithDomainType))
                .build());

        builder.type(newTypeWiring(GraphQLConstants.MUTATION_TYPE_NAME)
                .defaultDataFetcher(
                        new GraphQLMutationDataFetcher(
                                repository, objectMapper, queryService, graphQLFieldReferencesWithDomainType))
                .build());

        return builder.build();
    }

    @Override
    public ExecutionResult graphQL(QueryContext queryContext, String query) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .context(queryContext)
                .build();
        return this.graphQL.execute(executionInput);
    }

    @Override
    public ExecutionResult graphQL(
            QueryContext queryContext, String query, String operationName, Map<String, Object> variables) {
        DataLoader<String, Map<String, Object>> defaultDataLoader = DataLoader.newDataLoader(
                new GraphQLBatchLoader(queryService),
                DataLoaderOptions
                        .newOptions()
                        .setBatchLoaderContextProvider(
                                new GraphQLBatchLoaderContextProvider(queryContext)));

        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register(GraphQLConstants.QUERY_DEFAULT_DATA_LOADER_KEY, defaultDataLoader);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .context(queryContext)
                .operationName(operationName)
                .variables(variables)
                .dataLoaderRegistry(dataLoaderRegistry)
                .build();

        return this.graphQL.execute(executionInput);
    }

}
