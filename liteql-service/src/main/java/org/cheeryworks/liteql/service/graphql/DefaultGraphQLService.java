package org.cheeryworks.liteql.service.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.language.FieldDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.graphql.Scalars;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.util.StringUtil;
import org.cheeryworks.liteql.model.util.graphql.GraphQLConstants;
import org.cheeryworks.liteql.service.GraphQLSchemaProcessor;
import org.cheeryworks.liteql.service.GraphQLService;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class DefaultGraphQLService implements GraphQLService {

    private static final String EMPTY_SCHEMA;

    private Repository repository;

    private ObjectMapper objectMapper;

    private QueryService queryService;

    private Scalars scalars;

    private GraphQLQueryDataFetcher graphQLQueryDataFetcher;

    private GraphQLMutationDataFetcher graphQLMutationDataFetcher;

    private DataLoaderRegistry dataLoaderRegistry;

    List<GraphQLSchemaProcessor> graphQLSchemaProcessors;

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
            List<GraphQLSchemaProcessor> graphQLSchemaProcessors) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.queryService = queryService;
        this.graphQLSchemaProcessors = graphQLSchemaProcessors;
        this.scalars = new Scalars(objectMapper);

        this.graphQLQueryDataFetcher = new GraphQLQueryDataFetcher(repository, objectMapper, queryService);
        this.graphQLMutationDataFetcher = new GraphQLMutationDataFetcher(repository, objectMapper, queryService);

        DataLoader<String, Map<String, Object>> defaultDataLoader
                = DataLoader.newDataLoader(new GraphQLBatchLoader(queryService));

        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register(GraphQLConstants.QUERY_DEFAULT_DATA_LOADER_KEY, defaultDataLoader);

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

            if (CollectionUtils.isNotEmpty(graphQLSchemaProcessors)) {
                for (GraphQLSchemaProcessor graphQLSchemaProcessor : graphQLSchemaProcessors) {
                    graphQLSchemaProcessor.process(
                            repository, scalars, typeDefinitionRegistry, graphQLFieldReferencesWithDomainType);
                }
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
                        typeRuntimeWiringBuilder.dataFetcher(complexField, this.graphQLQueryDataFetcher);
                    }

                    builder.type(typeRuntimeWiringBuilder.build());
                }
            }
        }

        builder.type(newTypeWiring(GraphQLConstants.QUERY_TYPE_NAME)
                .defaultDataFetcher(this.graphQLQueryDataFetcher)
                .build());

        builder.type(newTypeWiring(GraphQLConstants.MUTATION_TYPE_NAME)
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
