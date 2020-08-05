package org.cheeryworks.liteql.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionOperator;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.query.save.SaveQueryAssociations;
import org.cheeryworks.liteql.schema.SchemaDefinitionProvider;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.TypeDefinition;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;
import org.cheeryworks.liteql.schema.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.util.jackson.deserializer.ConditionClauseDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.ConditionOperatorDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.ConditionTypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.DataTypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.FieldDefinitionsDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.FieldDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.MigrationOperationDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.MigrationOperationTypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.PublicQueryDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.QueryConditionsDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.QueryTypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.SaveQueryAssociationsDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.TypeDefinitionDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.TypeNameDeserializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionClauseSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionOperatorSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.DataTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.MigrationOperationTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.QueryTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.TypeNameSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.TypeDefinitionSerializer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

public abstract class LiteQL {

    private LiteQL() {

    }

    public static final class Constants {

        public static final String SPECIFICATION_VERSION = "1.0.0";

        public static final String SCHEMA = "liteql";

        public static final String WORD_CONCAT = "_";

        public static final String NAME_CONCAT = ".";

        public static final String DEFAULT_CUSTOMIZED_CONFIGURATION_PATH
                = System.getProperty("user.home") + File.separator + ".liteql";

        public static final String PLATFORM_VERSION_SPECIFIED_CUSTOMIZED_CONFIGURATION_PATH
                = DEFAULT_CUSTOMIZED_CONFIGURATION_PATH + File.separator + SPECIFICATION_VERSION;

        public static final String LITEQL_PROFILE_KEY = "liteql.profile";

        private Constants() {

        }

        public static final class GraphQL {
            public static final String QUERY_TYPE_NAME = "Query";

            public static final String QUERY_ARGUMENT_NAME_ID = IdField.ID_FIELD_NAME;

            public static final String QUERY_ARGUMENT_NAME_CONDITIONS = "conditions";

            public static final String QUERY_ARGUMENT_NAME_ORDER_BY = "order_by";

            public static final String QUERY_ARGUMENT_NAME_PAGINATION_OFFSET = "offset";

            public static final String QUERY_ARGUMENT_NAME_PAGINATION_FIRST = "first";

            public static final String MUTATION_TYPE_NAME = "Mutation";

            public static final String MUTATION_NAME_PREFIX_CREATE = "create_";

            public static final String MUTATION_NAME_PREFIX_UPDATE = "update_";

            public static final String QUERY_DOMAIN_TYPE_NAME_KEY = "domain_type_name";

            public static final String QUERY_DATA_FETCHING_ENVIRONMENT_KEY = "data_fetching_environment";

            public static final String QUERY_DATA_FETCHING_KEYS_KEY = "data_fetching_keys";

            public static final String QUERY_DEFAULT_DATA_LOADER_KEY = "default";

            public static final String SCALAR_LONG_NAME = "Long";

            public static final String SCALAR_DECIMAL_NAME = "Decimal";

            public static final String SCALAR_TIMESTAMP_NAME = "Timestamp";

            public static final String SCALAR_CONDITION_VALUE_NAME = "ConditionValue";

            public static final String INPUT_TYPE_NAME_SUFFIX = "Input";
        }

    }

    public static final class SchemaUtils {

        private static Set<SchemaDefinitionProvider> schemaDefinitionProviders = new HashSet<>();

        private static Set<String> schemaDefinitionPackages = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        static {
            Iterator<SchemaDefinitionProvider> schemaDefinitionProviderIterator
                    = ServiceLoader.load(SchemaDefinitionProvider.class).iterator();

            while (schemaDefinitionProviderIterator.hasNext()) {
                SchemaDefinitionProvider schemaDefinitionProvider = schemaDefinitionProviderIterator.next();

                for (SchemaDefinitionProvider existSchemaDefinitionProvider : schemaDefinitionProviders) {
                    if (existSchemaDefinitionProvider.getSchema()
                            .equalsIgnoreCase(schemaDefinitionProvider.getSchema())) {
                        throw new IllegalStateException("Same schema [" + schemaDefinitionProvider.getSchema() + "]" +
                                " defined both in SchemaDefinitionProvider [" +
                                existSchemaDefinitionProvider.getClass().getName() + "] and [" +
                                schemaDefinitionProvider.getClass().getName() + "]");
                    }

                    for (String existPackageName : existSchemaDefinitionProvider.getPackages()) {
                        if (Arrays.stream(schemaDefinitionProvider.getPackages())
                                .anyMatch(packageName -> packageName.equals(existPackageName))) {
                            throw new IllegalStateException("Same package [" + existPackageName + "]" +
                                    " defined both in SchemaDefinitionProvider [" +
                                    existSchemaDefinitionProvider.getClass().getName() + "] and [" +
                                    schemaDefinitionProvider.getClass().getName() + "]");
                        }
                    }
                }

                schemaDefinitionProviders.add(schemaDefinitionProvider);

                schemaDefinitionPackages.addAll(Arrays.asList(schemaDefinitionProvider.getPackages()));
            }
        }

        public static Set<String> getSchemaDefinitionPackages() {
            return schemaDefinitionPackages;
        }

        public static String getSchemaOfTrait(Class<? extends TraitType> traitType) {
            return getProperty(traitType, SchemaDefinitionProvider::getSchema);
        }

        public static String getVersionOfTrait(Class<? extends TraitType> traitType) {
            return getProperty(traitType, SchemaDefinitionProvider::getVersion);
        }

        private static <T> T getProperty(
                Class<? extends TraitType> traitType, Function<SchemaDefinitionProvider, T> consumer) {
            for (SchemaDefinitionProvider schemaDefinitionProvider : schemaDefinitionProviders) {
                boolean matched = Arrays.stream(schemaDefinitionProvider.getPackages())
                        .anyMatch(schemaDefinitionPackage -> schemaDefinitionPackage.equals(
                                traitType.getPackage().getName()));

                if (matched) {
                    return consumer.apply(schemaDefinitionProvider);
                }
            }

            return null;
        }

        public static TypeName getTypeName(Class<? extends TraitType> traitType) {
            String schema = getSchemaOfTrait(traitType);

            if (org.apache.commons.lang3.StringUtils.isNotBlank(schema)) {
                TypeName typeName = new TypeName();
                typeName.setSchema(getSchemaOfTrait(traitType));
                typeName.setName(
                        StringUtils.camelNameToLowerDashConnectedLowercaseName(traitType.getSimpleName()));

                return typeName;
            }

            return null;
        }

        public static TypeName getTypeName(String typeNameInString) {
            String[] typeNameParts = typeNameInString.split("\\.");

            if (typeNameParts.length != 2) {
                throw new IllegalArgumentException("Invalid type name [" + typeNameInString + "]");
            }

            TypeName typeName = new TypeName();
            typeName.setSchema(typeNameParts[0]);
            typeName.setName(typeNameParts[1]);

            return typeName;
        }

        public static Class<? extends TraitType> getTraitType(String traitTypeName) {
            Class<?> javaType = ClassUtils.getClass(traitTypeName);

            if (TraitType.class.equals(javaType) || !TraitType.class.isAssignableFrom(javaType)) {
                throw new IllegalArgumentException("Sub type of Trait is Required");
            }

            return (Class<? extends TraitType>) javaType;
        }

    }

    public static final class StringUtils {

        private StringUtils() {

        }

        public static java.lang.String camelNameToLowerDashConnectedLowercaseName(java.lang.String camelName) {
            java.lang.String[] words = org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(camelName);

            return java.lang.String.join("_", words).toLowerCase();
        }

        public static String plural(String word) {
            return English.plural(word);
        }

        public static Set<String> convertDelimitedParameterToSetOfString(String ids) {
            Set<String> selectedIds = new LinkedHashSet<>();
            if (!org.apache.commons.lang3.StringUtils.isEmpty(ids)) {
                String[] idsInArray = ids.split("[,]");
                for (String id : idsInArray) {
                    selectedIds.add(id);
                }
            }

            return selectedIds;
        }

        public static Set<Long> convertDelimitedParameterToSetOfLong(String ids) {
            Set<Long> selectedIds = new HashSet<>();
            if (!org.apache.commons.lang3.StringUtils.isEmpty(ids)) {
                String[] idsInArray = ids.split("[,]");
                for (String id : idsInArray) {
                    selectedIds.add(Long.parseLong(id));
                }
            }

            return selectedIds;
        }

    }

    public static final class ClassUtils {

        private ClassUtils() {

        }

        public static Class<?> getClass(String className) {
            try {
                return Class.forName(className);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

    }

    public static final class JacksonJsonUtils {
        public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        static {
            OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
            OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            OBJECT_MAPPER.registerModule(getLiteQLJsonModule());
        }

        private JacksonJsonUtils() {

        }

        public static SimpleModule getLiteQLJsonModule() {
            SimpleModule module = new SimpleModule();

            module.addDeserializer(TypeName.class, new TypeNameDeserializer());
            module.addDeserializer(TypeDefinition.class, new TypeDefinitionDeserializer());
            module.addDeserializer(Field.class, new FieldDeserializer());
            module.addDeserializer(MigrationOperation.class, new MigrationOperationDeserializer());
            module.addDeserializer(MigrationOperationType.class, new MigrationOperationTypeDeserializer());
            module.addDeserializer(QueryType.class, new QueryTypeDeserializer());
            module.addDeserializer(DataType.class, new DataTypeDeserializer());
            module.addDeserializer(FieldDefinitions.class, new FieldDefinitionsDeserializer());
            module.addDeserializer(QueryConditions.class, new QueryConditionsDeserializer());
            module.addDeserializer(ConditionClause.class, new ConditionClauseDeserializer());
            module.addDeserializer(ConditionOperator.class, new ConditionOperatorDeserializer());
            module.addDeserializer(ConditionType.class, new ConditionTypeDeserializer());
            module.addDeserializer(SaveQueryAssociations.class, new SaveQueryAssociationsDeserializer());
            module.addDeserializer(PublicQuery.class, new PublicQueryDeserializer());

            module.addSerializer(TypeName.class, new TypeNameSerializer());
            module.addSerializer(TypeDefinition.class, new TypeDefinitionSerializer());
            module.addSerializer(MigrationOperationType.class, new MigrationOperationTypeSerializer());
            module.addSerializer(QueryType.class, new QueryTypeSerializer());
            module.addSerializer(DataType.class, new DataTypeSerializer());
            module.addSerializer(ConditionClause.class, new ConditionClauseSerializer());
            module.addSerializer(ConditionOperator.class, new ConditionOperatorSerializer());
            module.addSerializer(ConditionType.class, new ConditionTypeSerializer());

            return module;
        }

        public static <T> T toBean(String content, Class<T> clazz) {
            T bean = null;

            if (org.apache.commons.lang3.StringUtils.isNotBlank(content)) {
                try {
                    bean = OBJECT_MAPPER.readValue(content, clazz);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("\"" + content + "\" is not valid JSON", ex);
                }
            }

            return bean;
        }

        public static <T> String toJson(T bean) {
            try {
                return OBJECT_MAPPER.writeValueAsString(bean);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

        public static JsonNode toJsonNode(ObjectMapper objectMapper, String content) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(content)) {
                try {
                    return objectMapper.readTree(content);
                } catch (IOException ex) {
                    throw new IllegalArgumentException(ex.getMessage(), ex);
                }
            }

            return null;
        }
    }

}
