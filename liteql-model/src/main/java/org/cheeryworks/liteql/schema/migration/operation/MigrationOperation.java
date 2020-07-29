package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;

import java.io.Serializable;

public interface MigrationOperation extends Serializable {

    MigrationOperationType getType();

    void merge(DomainType domainType);

}
