package org.cheeryworks.liteql.service.schema;

import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.service.LiteQLService;

import java.util.Map;
import java.util.Set;

public interface SchemaService extends LiteQLService {

    String SUFFIX_OF_SCHEMA_ROOT_FILE = ".yml";

    String NAME_OF_TYPES_DIRECTORY = "types";

    String NAME_OF_MIGRATIONS_DIRECTORY = "migrations";

    String SUFFIX_OF_CONFIGURATION_FILE = ".json";

    String NAME_OF_TYPE_DEFINITION = "definition" + SUFFIX_OF_CONFIGURATION_FILE;

    Set<String> getSchemaNames();

    Set<DomainType> getDomainTypes(String schemaName);

    Set<TraitType> getTraitTypes(String schemaName);

    DomainType getDomainType(TypeName typeName);

    TraitType getTraitType(TypeName typeName);

    Map<TypeName, Map<String, Migration>> getMigrations(String schemaName);

}
