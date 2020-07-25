package org.cheeryworks.liteql.util.graphql;

import org.cheeryworks.liteql.schema.field.IdField;

public abstract class GraphQLConstants {

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
