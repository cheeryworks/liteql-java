package org.cheeryworks.liteql.model.type.migration.operation;

import org.cheeryworks.liteql.model.enums.MigrationOperationType;

import java.io.Serializable;

public interface MigrationOperation extends Serializable {

    MigrationOperationType getType();

}
