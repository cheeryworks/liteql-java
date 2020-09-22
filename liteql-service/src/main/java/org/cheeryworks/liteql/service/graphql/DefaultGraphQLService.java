package org.cheeryworks.liteql.service.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumValueDefinition;
import graphql.language.FieldDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.SchemaDefinition;
import graphql.language.Type;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.graphql.Scalars;
import org.cheeryworks.liteql.query.QueryCondition;
import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionOperator;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.query.enums.Direction;
import org.cheeryworks.liteql.query.read.sort.QuerySort;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.field.AbstractNullableField;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.ReferenceField;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.util.GraphQLServiceUtil;
import org.cheeryworks.liteql.util.LiteQL;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.INPUT_TYPE_NAME_SUFFIX;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.MUTATION_NAME_PREFIX_CREATE;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.MUTATION_TYPE_NAME;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_CONDITIONS;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ORDER_BY;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_PAGINATION_FIRST;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_PAGINATION_OFFSET;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.QUERY_DEFAULT_DATA_LOADER_KEY;
import static org.cheeryworks.liteql.util.LiteQL.Constants.GraphQL.QUERY_TYPE_NAME;

public class DefaultGraphQLService implements GraphQLService {

    private SchemaService schemaService;

    private GraphQLQueryDataFetcher graphQLQueryDataFetcher;

    private GraphQLMutationDataFetcher graphQLMutationDataFetcher;

    private DataLoaderRegistry dataLoaderRegistry;

    private GraphQL graphQL;

    public DefaultGraphQLService(SchemaService schemaService, QueryService queryService) {
        this.schemaService = schemaService;

        this.graphQLQueryDataFetcher = new GraphQLQueryDataFetcher(schemaService, queryService);
        this.graphQLMutationDataFetcher = new GraphQLMutationDataFetcher(schemaService, queryService);

        DataLoader<String, Map<String, Object>> defaultDataLoader
                = DataLoader.newDataLoader(new GraphQLBatchLoader(queryService));

        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register(QUERY_DEFAULT_DATA_LOADER_KEY, defaultDataLoader);

        this.dataLoaderRegistry = dataLoaderRegistry;

        try {
            GraphQLSchema graphQLSchema = buildingSchema();

            this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        } catch (Exception ex) {
            throw new IllegalStateException("GraphQL initializing failed", ex);
        }
    }

    private GraphQLSchema buildingSchema() {
        try {
            SchemaGenerator schemaGenerator = new SchemaGenerator();

            TypeDefinitionRegistry typeDefinitionRegistry = buildTypeDefinitionRegistry();

            RuntimeWiring runtimeWiring = buildRuntimeWiring(typeDefinitionRegistry.types());

            return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private TypeDefinitionRegistry buildTypeDefinitionRegistry() {
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();

        buildSchemaDefinition(typeDefinitionRegistry);

        buildEnumDefinitions(typeDefinitionRegistry);

        buildScalarDefinitions(typeDefinitionRegistry);

        buildObjectTypeDefinitions(schemaService, typeDefinitionRegistry);

        buildDefaultQueries(typeDefinitionRegistry);

        buildDefaultInputTypes(typeDefinitionRegistry);

        buildDefaultMutations(typeDefinitionRegistry);

        return typeDefinitionRegistry;
    }

    public void buildSchemaDefinition(TypeDefinitionRegistry typeDefinitionRegistry) {
        typeDefinitionRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition()
                .name(QUERY_TYPE_NAME)
                .build());

        typeDefinitionRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition()
                .name(MUTATION_TYPE_NAME)
                .build());

        typeDefinitionRegistry.add(
                SchemaDefinition.newSchemaDefinition()
                        .operationTypeDefinition(
                                OperationTypeDefinition.newOperationTypeDefinition()
                                        .name(QUERY_TYPE_NAME.toLowerCase())
                                        .typeName(new TypeName(QUERY_TYPE_NAME))
                                        .build())
                        .operationTypeDefinition(OperationTypeDefinition.newOperationTypeDefinition()
                                .name(MUTATION_TYPE_NAME.toLowerCase())
                                .typeName(new TypeName(MUTATION_TYPE_NAME))
                                .build())
                        .build());
    }

    public void buildEnumDefinitions(TypeDefinitionRegistry typeDefinitionRegistry) {
        buildEumTypeDefinition(typeDefinitionRegistry, ConditionOperator.class, ConditionOperator.values());

        buildEumTypeDefinition(typeDefinitionRegistry, ConditionClause.class, ConditionClause.values());

        buildEumTypeDefinition(typeDefinitionRegistry, ConditionType.class, ConditionType.values());

        buildEumTypeDefinition(typeDefinitionRegistry, Direction.class, Direction.values());
    }

    private void buildEumTypeDefinition(
            TypeDefinitionRegistry typeDefinitionRegistry, Class enumerationType, Enum[] enumerations) {
        List<EnumValueDefinition> enumValueDefinitions = new ArrayList<>();

        for (Enum enumeration : enumerations) {
            enumValueDefinitions.add(EnumValueDefinition
                    .newEnumValueDefinition()
                    .name(enumeration.name())
                    .build());
        }

        typeDefinitionRegistry.add(
                EnumTypeDefinition.newEnumTypeDefinition()
                        .name(enumerationType.getSimpleName())
                        .enumValueDefinitions(enumValueDefinitions)
                        .build());
    }

    public void buildScalarDefinitions(TypeDefinitionRegistry typeDefinitionRegistry) {
        typeDefinitionRegistry.add(
                ScalarTypeDefinition.newScalarTypeDefinition()
                        .name(Scalars.LONG.getName())
                        .build());

        typeDefinitionRegistry.add(
                ScalarTypeDefinition.newScalarTypeDefinition()
                        .name(Scalars.DECIMAL.getName())
                        .build());

        typeDefinitionRegistry.add(
                ScalarTypeDefinition.newScalarTypeDefinition()
                        .name(Scalars.TIMESTAMP.getName())
                        .build());

        typeDefinitionRegistry.add(
                ScalarTypeDefinition.newScalarTypeDefinition()
                        .name(Scalars.CONDITION_VALUE.getName())
                        .build());
    }

    public void buildObjectTypeDefinitions(
            SchemaService schemaService, TypeDefinitionRegistry typeDefinitionRegistry) {
        Map<String, ObjectTypeDefinition.Builder> objectTypeDefinitions = new HashMap<>();

        for (String schema : schemaService.getSchemaNames()) {
            for (DomainTypeDefinition domainTypeDefinition : schemaService.getDomainTypeDefinitions(schema)) {
                if (!domainTypeDefinition.isGraphQLType()) {
                    continue;
                }

                String objectTypeName = GraphQLServiceUtil.toObjectTypeName(domainTypeDefinition.getTypeName());

                ObjectTypeDefinition.Builder objectTypeDefinitionBuilder = ObjectTypeDefinition
                        .newObjectTypeDefinition()
                        .name(objectTypeName);

                objectTypeDefinitions.put(objectTypeName, objectTypeDefinitionBuilder);

                for (Field field : domainTypeDefinition.getFields()) {
                    if (!field.isGraphQLField()) {
                        continue;
                    }

                    FieldDefinition.Builder fieldDefinitionBuilder = FieldDefinition
                            .newFieldDefinition()
                            .name(field.getName())
                            .type(getGraphQLTypeFromField(field))
                            .inputValueDefinitions(defaultFieldArguments());

                    FieldDefinition fieldDefinition = fieldDefinitionBuilder.build();

                    objectTypeDefinitionBuilder.fieldDefinition(fieldDefinition);
                }
            }
        }

        objectTypeDefinitions.values().stream().map(x -> x.build()).forEach(typeDefinitionRegistry::add);
    }

    private Type getGraphQLTypeFromField(Field field) {
        String typeName = getGraphQLTypeNameFromField(field);

        if (field instanceof AbstractNullableField) {
            AbstractNullableField nullableField = (AbstractNullableField) field;

            if (!nullableField.isNullable()) {
                return new NonNullType(new TypeName(typeName));
            }
        }

        return new TypeName(typeName);
    }

    private String getGraphQLTypeNameFromField(Field field) {
        switch (field.getType()) {
            case Id:
            case Clob:
            case String:
                return graphql.Scalars.GraphQLString.getName();
            case Long:
                return Scalars.LONG.getName();
            case Integer:
                return graphql.Scalars.GraphQLInt.getName();
            case Timestamp:
                return Scalars.TIMESTAMP.getName();
            case Boolean:
                return graphql.Scalars.GraphQLBoolean.getName();
            case Decimal:
                return Scalars.DECIMAL.getName();
            case Blob:
                return graphql.Scalars.GraphQLByte.getName();
            case Reference:
                ReferenceField referenceField
                        = (ReferenceField) field;

                org.cheeryworks.liteql.schema.TypeName traitImplement
                        = schemaService.getTraitImplement(referenceField.getDomainTypeName());

                if (traitImplement != null) {
                    return GraphQLServiceUtil.toObjectTypeName(traitImplement);
                }

                return GraphQLServiceUtil.toObjectTypeName(referenceField.getDomainTypeName());
            default:
                throw new IllegalArgumentException("Unsupported field type: " + field.getType().name());
        }
    }

    private List<InputValueDefinition> defaultFieldArguments() {
        List<InputValueDefinition> arguments = new ArrayList<>();

        arguments.add(
                InputValueDefinition.newInputValueDefinition()
                        .name(QUERY_ARGUMENT_NAME_CONDITIONS)
                        .type(new ListType(
                                new NonNullType(
                                        new TypeName(QueryCondition.class.getSimpleName()))))
                        .build());

        arguments.add(
                InputValueDefinition.newInputValueDefinition()
                        .name(QUERY_ARGUMENT_NAME_ORDER_BY)
                        .type(new ListType(
                                new NonNullType(
                                        new TypeName(QuerySort.class.getSimpleName()))))
                        .build());

        return arguments;
    }

    private void buildDefaultInputTypes(
            TypeDefinitionRegistry typeDefinitionRegistry) {
        List<InputValueDefinition> inputValueDefinitions = new ArrayList<>();

        java.lang.reflect.Field[] fields = QueryCondition.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (field.getName().equalsIgnoreCase("field")) {
                inputValueDefinitions.add(
                        InputValueDefinition.newInputValueDefinition()
                                .name(field.getName())
                                .type(new NonNullType(new TypeName(field.getType().getSimpleName())))
                                .build());
            } else if (field.getName().equalsIgnoreCase("value")) {
                inputValueDefinitions.add(
                        InputValueDefinition.newInputValueDefinition()
                                .name(field.getName())
                                .type(new TypeName(Scalars.CONDITION_VALUE.getName()))
                                .build());
            } else if (field.getName().equalsIgnoreCase("conditions")) {
                inputValueDefinitions.add(
                        InputValueDefinition.newInputValueDefinition()
                                .name(field.getName())
                                .type(new ListType(new TypeName(QueryCondition.class.getSimpleName())))
                                .build());
            } else {
                inputValueDefinitions.add(
                        InputValueDefinition.newInputValueDefinition()
                                .name(field.getName())
                                .type(new TypeName(field.getType().getSimpleName()))
                                .build());
            }
        }

        typeDefinitionRegistry.add(InputObjectTypeDefinition.newInputObjectDefinition()
                .name(QueryCondition.class.getSimpleName())
                .inputValueDefinitions(inputValueDefinitions)
                .build());

        inputValueDefinitions = new ArrayList<>();

        fields = QuerySort.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (field.getName().equalsIgnoreCase("field")) {
                inputValueDefinitions.add(
                        InputValueDefinition.newInputValueDefinition()
                                .name(field.getName())
                                .type(new NonNullType(new TypeName(field.getType().getSimpleName())))
                                .build());
            } else {
                inputValueDefinitions.add(
                        InputValueDefinition.newInputValueDefinition()
                                .name(field.getName())
                                .type(new TypeName(field.getType().getSimpleName()))
                                .build());
            }
        }

        typeDefinitionRegistry.add(InputObjectTypeDefinition.newInputObjectDefinition()
                .name(QuerySort.class.getSimpleName())
                .inputValueDefinitions(inputValueDefinitions)
                .build());

        for (Map.Entry<String, TypeDefinition> typeEntry : typeDefinitionRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition) {
                InputObjectTypeDefinition.Builder inputObjectTypeDefinitionBuilder = InputObjectTypeDefinition
                        .newInputObjectDefinition()
                        .name(typeEntry.getKey() + INPUT_TYPE_NAME_SUFFIX);

                processInputTypeFields(
                        inputObjectTypeDefinitionBuilder,
                        typeDefinitionRegistry.types(),
                        ((ObjectTypeDefinition) typeEntry.getValue()).getFieldDefinitions());

                typeDefinitionRegistry.add(inputObjectTypeDefinitionBuilder.build());
            }
        }
    }

    private void processInputTypeFields(
            InputObjectTypeDefinition.Builder inputObjectTypeDefinitionBuilder,
            Map<String, TypeDefinition> typeDefinitions, List<FieldDefinition> fieldDefinitions) {
        for (int i = 0; i < fieldDefinitions.size(); i++) {
            FieldDefinition fieldDefinition = fieldDefinitions.get(i);

            if (fieldDefinition.getType() instanceof TypeName) {
                TypeName typeName = (TypeName) fieldDefinition.getType();

                processTypeInInputType(
                        inputObjectTypeDefinitionBuilder, typeDefinitions, fieldDefinition, typeName);
            } else if (fieldDefinition.getType() instanceof NonNullType) {
                if (((NonNullType) fieldDefinition.getType()).getType() instanceof TypeName) {
                    TypeName typeName = (TypeName) ((NonNullType) fieldDefinition.getType()).getType();

                    if (!typeName.getName().equals(graphql.Scalars.GraphQLID.getName())) {
                        processTypeInInputType(
                                inputObjectTypeDefinitionBuilder, typeDefinitions, fieldDefinition, typeName);
                    }
                } else if (((NonNullType) fieldDefinition.getType()).getType() instanceof ListType) {
                    ListType listType = (ListType) ((NonNullType) fieldDefinition.getType()).getType();

                    processListTypeInInputType(
                            inputObjectTypeDefinitionBuilder, typeDefinitions, fieldDefinition, listType);
                }
            } else if (fieldDefinition.getType() instanceof ListType) {
                ListType listType = (ListType) fieldDefinition.getType();

                processListTypeInInputType(
                        inputObjectTypeDefinitionBuilder, typeDefinitions, fieldDefinition, listType);
            }
        }
    }

    private void processTypeInInputType(
            InputObjectTypeDefinition.Builder inputObjectTypeDefinitionBuilder,
            Map<String, TypeDefinition> typeDefinitions, FieldDefinition fieldDefinition, TypeName typeName) {
        inputObjectTypeDefinitionBuilder.inputValueDefinition(
                InputValueDefinition.newInputValueDefinition()
                        .name(fieldDefinition.getName())
                        .type(typeDefinitions.containsKey(typeName.getName())
                                ? new TypeName(typeName.getName() + INPUT_TYPE_NAME_SUFFIX)
                                : typeName)
                        .build());
    }

    private void processListTypeInInputType(
            InputObjectTypeDefinition.Builder inputObjectTypeDefinitionBuilder,
            Map<String, TypeDefinition> typeDefinitions,
            FieldDefinition fieldDefinition, ListType listType) {
        TypeName typeName;

        if (listType.getType() instanceof NonNullType) {
            typeName = (TypeName) ((NonNullType) listType.getType()).getType();
        } else {
            typeName = (TypeName) listType.getType();
        }

        inputObjectTypeDefinitionBuilder.inputValueDefinition(InputValueDefinition.newInputValueDefinition()
                .name(fieldDefinition.getName())
                .type(typeDefinitions.containsKey(typeName.getName())
                        ? new ListType(new TypeName(typeName.getName() + INPUT_TYPE_NAME_SUFFIX))
                        : new ListType(typeName))
                .build());
    }

    private void buildDefaultQueries(
            TypeDefinitionRegistry typeDefinitionRegistry) {
        for (Map.Entry<String, TypeDefinition> typeEntry : typeDefinitionRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition) {
                ObjectTypeExtensionDefinition objectTypeExtensionDefinition = ObjectTypeExtensionDefinition
                        .newObjectTypeExtensionDefinition()
                        .name(QUERY_TYPE_NAME)
                        .fieldDefinition(
                                FieldDefinition
                                        .newFieldDefinition()
                                        .name(StringUtils.uncapitalize(typeEntry.getKey()))
                                        .inputValueDefinition(
                                                InputValueDefinition
                                                        .newInputValueDefinition()
                                                        .name(QUERY_ARGUMENT_NAME_ID)
                                                        .type(
                                                                new NonNullType(
                                                                        new TypeName(
                                                                                graphql.Scalars.GraphQLID.getName())))
                                                        .build())
                                        .type(new NonNullType(new TypeName(typeEntry.getKey())))
                                        .build())
                        .fieldDefinition(FieldDefinition
                                .newFieldDefinition()
                                .name(StringUtils.uncapitalize(LiteQL.StringUtils.plural(typeEntry.getKey())))
                                .inputValueDefinition(
                                        InputValueDefinition
                                                .newInputValueDefinition()
                                                .name(QUERY_ARGUMENT_NAME_CONDITIONS)
                                                .type(
                                                        new ListType(
                                                                new NonNullType(
                                                                        new TypeName(
                                                                                QueryCondition.class.getSimpleName()))))
                                                .build())
                                .inputValueDefinition(
                                        InputValueDefinition
                                                .newInputValueDefinition()
                                                .name(QUERY_ARGUMENT_NAME_ORDER_BY)
                                                .type(
                                                        new ListType(
                                                                new NonNullType(
                                                                        new TypeName(
                                                                                QuerySort.class.getSimpleName()))))
                                                .build())
                                .inputValueDefinition(
                                        InputValueDefinition
                                                .newInputValueDefinition()
                                                .name(QUERY_ARGUMENT_NAME_PAGINATION_OFFSET)
                                                .type(new TypeName(graphql.Scalars.GraphQLInt.getName()))
                                                .build())
                                .inputValueDefinition(
                                        InputValueDefinition
                                                .newInputValueDefinition()
                                                .name(QUERY_ARGUMENT_NAME_PAGINATION_FIRST)
                                                .type(new TypeName(graphql.Scalars.GraphQLInt.getName()))
                                                .build())
                                .type(new ListType(new TypeName(typeEntry.getKey())))
                                .build())
                        .build();

                typeDefinitionRegistry.add(objectTypeExtensionDefinition);
            }
        }
    }

    private void buildDefaultMutations(
            TypeDefinitionRegistry typeDefinitionRegistry) {
        for (Map.Entry<String, TypeDefinition> typeEntry : typeDefinitionRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition) {
                ObjectTypeExtensionDefinition.Builder objectTypeExtensionDefinitionBuilder
                        = ObjectTypeExtensionDefinition
                        .newObjectTypeExtensionDefinition()
                        .name(MUTATION_TYPE_NAME);

                FieldDefinition.Builder fieldDefinitionBuilder = FieldDefinition
                        .newFieldDefinition()
                        .name(MUTATION_NAME_PREFIX_CREATE + typeEntry.getKey())
                        .type(new TypeName(typeEntry.getKey()));

                processMutationInput(
                        fieldDefinitionBuilder,
                        typeDefinitionRegistry.types(),
                        ((ObjectTypeDefinition) typeEntry.getValue()).getFieldDefinitions());

                objectTypeExtensionDefinitionBuilder.fieldDefinition(fieldDefinitionBuilder.build());

                typeDefinitionRegistry.add(objectTypeExtensionDefinitionBuilder.build());
            }
        }
    }

    private void processMutationInput(
            FieldDefinition.Builder fieldDefinitionBuilder,
            Map<String, TypeDefinition> typeDefinitions, List<FieldDefinition> fieldDefinitions) {
        for (int i = 0; i < fieldDefinitions.size(); i++) {
            FieldDefinition fieldDefinition = fieldDefinitions.get(i);

            if (fieldDefinition.getType() instanceof TypeName) {
                TypeName typeName = (TypeName) fieldDefinition.getType();

                processMutationInput(fieldDefinitionBuilder, typeDefinitions, fieldDefinition, typeName);
            } else if (fieldDefinition.getType() instanceof NonNullType) {
                if (((NonNullType) fieldDefinition.getType()).getType() instanceof TypeName) {
                    TypeName typeName = (TypeName) ((NonNullType) fieldDefinition.getType()).getType();

                    if (!typeName.getName().equals(graphql.Scalars.GraphQLID.getName())) {
                        processMutationInput(fieldDefinitionBuilder, typeDefinitions, fieldDefinition, typeName);
                    }
                }
            }
        }
    }

    private void processMutationInput(
            FieldDefinition.Builder fieldDefinitionBuilder, Map<String, TypeDefinition> typeDefinitions,
            FieldDefinition fieldDefinition, TypeName typeName) {
        if (typeDefinitions.containsKey(typeName.getName())) {
            fieldDefinitionBuilder.inputValueDefinition(InputValueDefinition
                    .newInputValueDefinition()
                    .name(fieldDefinition.getName() + StringUtils.capitalize(QUERY_ARGUMENT_NAME_ID))
                    .type(new TypeName(graphql.Scalars.GraphQLID.getName()))
                    .build());
        } else {
            fieldDefinitionBuilder.inputValueDefinition(InputValueDefinition
                    .newInputValueDefinition()
                    .name(fieldDefinition.getName())
                    .type(new TypeName(typeName.getName()))
                    .build());
        }
    }

    private RuntimeWiring buildRuntimeWiring(Map<String, TypeDefinition> typeDefinitions) {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();

        builder.scalar(Scalars.LONG);
        builder.scalar(Scalars.DECIMAL);
        builder.scalar(Scalars.CONDITION_VALUE);
        builder.scalar(Scalars.TIMESTAMP);

        for (Map.Entry<String, TypeDefinition> typeEntry : typeDefinitions.entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition) {

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
                        typeRuntimeWiringBuilder.dataFetcher(complexField, this.graphQLQueryDataFetcher);
                    }

                    builder.type(typeRuntimeWiringBuilder.build());
                }
            }
        }

        builder.type(newTypeWiring(QUERY_TYPE_NAME)
                .defaultDataFetcher(this.graphQLQueryDataFetcher)
                .build());

        builder.type(newTypeWiring(MUTATION_TYPE_NAME)
                .defaultDataFetcher(this.graphQLMutationDataFetcher)
                .build());

        return builder.build();
    }

    @Override
    public ExecutionResult graphQL(QueryContext queryContext, String query) {
        return graphQL(queryContext, query, null, null);
    }

    @Override
    public ExecutionResult graphQL(
            QueryContext queryContext, String query, String operationName, Map<String, Object> variables) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(query)
                .context(queryContext)
                .dataLoaderRegistry(dataLoaderRegistry);

        if (StringUtils.isNotBlank(operationName)) {
            executionInputBuilder.operationName(operationName);
        }

        if (MapUtils.isNotEmpty(variables)) {
            executionInputBuilder.variables(variables);
        }

        return this.graphQL.execute(executionInputBuilder.build());
    }

}
