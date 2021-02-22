package org.cheeryworks.liteql.skeleton.service.schema;

import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.GraphQLTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.migration.Migration;

import java.util.Map;
import java.util.Set;

public interface SchemaService {

    Set<String> getSchemaNames();

    Set<DomainTypeDefinition> getDomainTypeDefinitions(String schemaName);

    DomainTypeDefinition getDomainTypeDefinition(TypeName typeName);

    Set<TraitTypeDefinition> getTraitTypeDefinitions(String schemaName);

    TraitTypeDefinition getTraitTypeDefinition(TypeName typeName);

    Map<TypeName, Map<String, Migration>> getMigrations(String schemaName);

    TypeName getTraitImplement(TypeName traitTypeName);

    Set<GraphQLTypeDefinition> getGraphQLTypeDefinitions();

    Set<GraphQLTypeDefinition> getGraphQLTypeDefinitions(String schemaName);

    GraphQLTypeDefinition getGraphQLTypeDefinition(TypeName typeName);

    Set<GraphQLTypeDefinition> getGraphQLTypeDefinitionsByExtensionTypeName(TypeName typeName);

    String export();

}
