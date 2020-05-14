package org.cheeryworks.liteql.service.repository;

import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.type.DomainType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GitRepository implements Repository {

    @Override
    public Set<String> getSchemas() {
        return null;
    }

    @Override
    public Map<String, DomainType> getDomainTypes(String schemaName) {
        return null;
    }

    @Override
    public DomainType getDomainType(String schemaName, String typeName) {
        return null;
    }

    @Override
    public List<Migration> getMigrations(String schemaName) {
        return null;
    }

}
