package org.cheeryworks.liteql.service.repository;

import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.StructType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.migration.Migration;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Repository {

    String SUFFIX_OF_SCHEMA_ROOT_FILE = ".yml";

    String NAME_OF_TYPES_DIRECTORY = "types";

    String NAME_OF_MIGRATIONS_DIRECTORY = "migrations";

    String SUFFIX_OF_CONFIGURATION_FILE = ".json";

    String NAME_OF_TYPE_DEFINITION = "definition" + SUFFIX_OF_CONFIGURATION_FILE;

    Set<String> getSchemas();

    Map<String, DomainType> getDomainTypes(String schemaName);

    Map<String, StructType> getStructTypes(String schemaName);

    DomainType getDomainType(TypeName typeName);

    StructType getStructType(TypeName typeName);

    List<Migration> getMigrations(String schemaName);

}
