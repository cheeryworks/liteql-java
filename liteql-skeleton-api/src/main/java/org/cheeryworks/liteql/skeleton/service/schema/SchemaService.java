package org.cheeryworks.liteql.skeleton.service.schema;

import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.migration.Migration;

import java.util.Map;
import java.util.Set;

public interface SchemaService {

    Set<String> getSchemaNames();

    Set<DomainTypeDefinition> getDomainTypeDefinitions(String schemaName);

    Set<TraitTypeDefinition> getTraitTypeDefinitions(String schemaName);

    DomainTypeDefinition getDomainTypeDefinition(TypeName typeName);

    TraitTypeDefinition getTraitTypeDefinition(TypeName typeName);

    Map<TypeName, Map<String, Migration>> getMigrations(String schemaName);

    TypeName getTraitImplement(TypeName traitTypeName);

    String export();

}
