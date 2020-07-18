package org.cheeryworks.liteql.service.graphql;

import graphql.language.FieldDefinition;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.cheeryworks.liteql.model.graphql.Scalars;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.field.AbstractNullableField;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.util.GraphQLServiceUtil;

import java.util.HashMap;
import java.util.Map;

public class DefaultGraphQLSchemaProcessor extends AbstractGraphQLSchemaProcessor {

    @Override
    public void process(
            Repository repository, Scalars scalars, TypeDefinitionRegistry typeDefinitionRegistry,
            Map<Class, Map<String, String>> graphQLFieldReferencesWithDomainType) {
        Map<String, ObjectTypeDefinition.Builder> objectTypeDefinitions = new HashMap<>();

        for (String schema : repository.getSchemaNames()) {
            for (DomainType domainType : repository.getDomainTypes(schema)) {
                if (!domainType.isGraphQLType()) {
                    continue;
                }

                String objectTypeName = GraphQLServiceUtil.getObjectTypeName(domainType);

                ObjectTypeDefinition.Builder objectTypeDefinitionBuilder = ObjectTypeDefinition
                        .newObjectTypeDefinition()
                        .name(objectTypeName);

                objectTypeDefinitions.put(objectTypeName, objectTypeDefinitionBuilder);

                for (Field field : domainType.getFields()) {
                    if (!field.isGraphQLField()) {
                        continue;
                    }

                    FieldDefinition.Builder fieldDefinitionBuilder = FieldDefinition
                            .newFieldDefinition()
                            .name(field.getName())
                            .type(getGraphQLTypeFromField(scalars, field))
                            .inputValueDefinitions(defaultFieldArguments());

                    FieldDefinition fieldDefinition = fieldDefinitionBuilder.build();

                    objectTypeDefinitionBuilder.fieldDefinition(fieldDefinition);
                }
            }
        }

        objectTypeDefinitions.values().stream().map(x -> x.build()).forEach(typeDefinitionRegistry::add);
    }

    private Type getGraphQLTypeFromField(Scalars scalars, Field field) {
        String typeName = getGraphQLTypeNameFromField(scalars, field);

        if (field instanceof AbstractNullableField) {
            AbstractNullableField nullableField = (AbstractNullableField) field;

            if (!nullableField.isNullable()) {
                return new NonNullType(new TypeName(typeName));
            }
        }

        return new TypeName(typeName);
    }

    private String getGraphQLTypeNameFromField(Scalars scalars, Field field) {
        switch (field.getType()) {
            case Id:
            case Clob:
            case String:
                return graphql.Scalars.GraphQLString.getName();
            case Long:
                return scalars.getScalarLong().getName();
            case Integer:
                return graphql.Scalars.GraphQLInt.getName();
            case Timestamp:
                return scalars.getScalarDate().getName();
            case Boolean:
                return graphql.Scalars.GraphQLBoolean.getName();
            case Decimal:
                return scalars.getScalarBigDecimal().getName();
            case Blob:
                return graphql.Scalars.GraphQLByte.getName();
            case Reference:
                org.cheeryworks.liteql.model.type.field.ReferenceField referenceField
                        = (org.cheeryworks.liteql.model.type.field.ReferenceField) field;
                return GraphQLServiceUtil.getObjectTypeName(referenceField.getDomainTypeName());
            default:
                throw new IllegalArgumentException("Unsupported field type: " + field.getType().name());
        }
    }


}
