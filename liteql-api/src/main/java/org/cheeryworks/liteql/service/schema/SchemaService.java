package org.cheeryworks.liteql.service.schema;

import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.Migration;

import java.util.Map;
import java.util.Set;

public interface SchemaService {

    Set<String> getSchemaNames();

    Set<DomainType> getDomainTypes(String schemaName);

    Set<TraitType> getTraitTypes(String schemaName);

    DomainType getDomainType(TypeName typeName);

    TraitType getTraitType(TypeName typeName);

    Map<TypeName, Map<String, Migration>> getMigrations(String schemaName);

    String export();

}
