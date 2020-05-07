package org.cheeryworks.liteql.service.repository;

import org.cheeryworks.liteql.model.migration.Migration;
import org.cheeryworks.liteql.model.type.DomainType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Repository {

    Set<String> getSchemas();

    Map<String, DomainType> getDomainTypes(String schemaName);

    DomainType getDomainType(String schemaName, String typeName);

    List<Migration> getMigrations(String schemaName);

}
