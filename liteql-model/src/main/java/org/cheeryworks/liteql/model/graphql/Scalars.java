package org.cheeryworks.liteql.model.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.cheeryworks.liteql.model.util.graphql.GraphQLConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

public class Scalars {

    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);

    private ObjectMapper objectMapper;

    private GraphQLScalarType scalarLong;
    private GraphQLScalarType scalarBigDecimal;
    private GraphQLScalarType scalarValue;
    private GraphQLScalarType scalarDate;

    public Scalars(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.scalarLong = buildScalarLong();
        this.scalarBigDecimal = buildScalarBigDecimal();
        this.scalarValue = buildScalarValue();
        this.scalarDate = buildScalarDate();
    }

    public GraphQLScalarType getScalarLong() {
        return scalarLong;
    }

    public GraphQLScalarType getScalarBigDecimal() {
        return scalarBigDecimal;
    }

    public GraphQLScalarType getScalarValue() {
        return scalarValue;
    }

    public GraphQLScalarType getScalarDate() {
        return scalarDate;
    }

    private boolean isNumberIsh(Object input) {
        return input instanceof Number || input instanceof String;
    }

    private String typeName(Object input) {
        if (input == null) {
            return "null";
        }

        return input.getClass().getSimpleName();
    }

    private GraphQLScalarType buildScalarLong() {
        return GraphQLScalarType.newScalar()
                .name(GraphQLConstants.SCALAR_LONG_NAME)
                .coercing(new Coercing<Long, Long>() {
                    private Long convertImpl(Object input) {
                        if (input instanceof Long) {
                            return (Long) input;
                        } else if (isNumberIsh(input)) {
                            BigDecimal value;
                            try {
                                value = new BigDecimal(input.toString());
                            } catch (NumberFormatException e) {
                                return null;
                            }
                            try {
                                return value.longValueExact();
                            } catch (ArithmeticException e) {
                                return null;
                            }
                        } else {
                            return null;
                        }

                    }

                    @Override
                    public Long serialize(Object input) {
                        Long result = convertImpl(input);
                        if (result == null) {
                            throw new CoercingSerializeException(
                                    "Expected type 'Long' but was '" + typeName(input) + "'."
                            );
                        }
                        return result;
                    }

                    @Override
                    public Long parseValue(Object input) {
                        Long result = convertImpl(input);
                        if (result == null) {
                            throw new CoercingParseValueException(
                                    "Expected type 'Long' but was '" + typeName(input) + "'."
                            );
                        }
                        return result;
                    }

                    @Override
                    public Long parseLiteral(Object input) {
                        if (input instanceof StringValue) {
                            try {
                                return Long.parseLong(((StringValue) input).getValue());
                            } catch (NumberFormatException e) {
                                throw new CoercingParseLiteralException(
                                        "Expected value to be a Long but it was '" + String.valueOf(input) + "'"
                                );
                            }
                        } else if (input instanceof IntValue) {
                            BigInteger value = ((IntValue) input).getValue();
                            if (value.compareTo(LONG_MIN) < 0 || value.compareTo(LONG_MAX) > 0) {
                                throw new CoercingParseLiteralException(
                                        "Expected value to be in the Long range but it was '" + value.toString() + "'"
                                );
                            }
                            return value.longValue();
                        }
                        throw new CoercingParseLiteralException(
                                "Expected AST type 'IntValue' or 'StringValue' but was '" + typeName(input) + "'."
                        );
                    }
                }).build();
    }

    private GraphQLScalarType buildScalarBigDecimal() {
        return GraphQLScalarType.newScalar()
                .name(GraphQLConstants.SCALAR_DECIMAL_NAME)
                .coercing(
                        new Coercing<BigDecimal, BigDecimal>() {
                            private BigDecimal convertImpl(Object input) {
                                if (isNumberIsh(input)) {
                                    try {
                                        return new BigDecimal(input.toString());
                                    } catch (NumberFormatException e) {
                                        return null;
                                    }
                                }
                                return null;

                            }

                            @Override
                            public BigDecimal serialize(Object input) {
                                BigDecimal result = convertImpl(input);
                                if (result == null) {
                                    throw new CoercingSerializeException(
                                            "Expected type 'BigDecimal' but was '" + typeName(input) + "'."
                                    );
                                }
                                return result;
                            }

                            @Override
                            public BigDecimal parseValue(Object input) {
                                BigDecimal result = convertImpl(input);
                                if (result == null) {
                                    throw new CoercingParseValueException(
                                            "Expected type 'BigDecimal' but was '" + typeName(input) + "'."
                                    );
                                }
                                return result;
                            }

                            @Override
                            public BigDecimal parseLiteral(Object input) {
                                if (input instanceof StringValue) {
                                    try {
                                        return new BigDecimal(((StringValue) input).getValue());
                                    } catch (NumberFormatException e) {
                                        throw new CoercingParseLiteralException(
                                                "Unable to turn AST input into a 'BigDecimal' : '" + input + "'"
                                        );
                                    }
                                } else if (input instanceof IntValue) {
                                    return new BigDecimal(((IntValue) input).getValue());
                                } else if (input instanceof FloatValue) {
                                    return ((FloatValue) input).getValue();
                                }
                                throw new CoercingParseLiteralException(
                                        "Expected AST type 'IntValue', 'StringValue' or 'FloatValue' but was '"
                                                + typeName(input) + "'."
                                );
                            }
                        }
                )
                .build();
    }

    private GraphQLScalarType buildScalarValue() {
        return GraphQLScalarType.newScalar()
                .name(GraphQLConstants.SCALAR_CONDITION_VALUE_NAME)
                .description("Condition Value Type")
                .coercing(
                        new Coercing() {
                            @Override
                            public Object serialize(Object dataFetcherResult) {
                                return dataFetcherResult;
                            }

                            @Override
                            public Object parseValue(Object input) {
                                return input;
                            }

                            @Override
                            public Object parseLiteral(Object input) {
                                return input;
                            }
                        }
                ).build();
    }

    private GraphQLScalarType buildScalarDate() {
        return GraphQLScalarType.newScalar()
                .name(GraphQLConstants.SCALAR_DATE_NAME)
                .description("Date Field")
                .coercing(
                        new Coercing() {
                            @Override
                            public Object serialize(Object dataFetcherResult) {
                                return objectMapper.getDateFormat().format(dataFetcherResult);
                            }

                            @Override
                            public Timestamp parseValue(Object input) {
                                try {
                                    return new Timestamp(
                                            objectMapper.getDateFormat().parse((String) input).getTime());
                                } catch (Exception ex) {
                                    throw new IllegalArgumentException("Parsing date failed, " + ex.getMessage());
                                }
                            }

                            @Override
                            public Date parseLiteral(Object input) {
                                try {
                                    if (!(input instanceof StringValue)) {
                                        return null;
                                    }

                                    String value = ((StringValue) input).getValue();
                                    return new Timestamp(objectMapper.getDateFormat().parse(value).getTime());
                                } catch (Exception ex) {
                                    throw new IllegalArgumentException("Parsing date failed, " + ex.getMessage());
                                }
                            }
                        }
                ).build();
    }

}
