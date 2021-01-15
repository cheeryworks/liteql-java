package org.cheeryworks.liteql.skeleton.schema.migration.operation;

import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;

import java.io.Serializable;

public interface MigrationOperation extends Serializable {

    MigrationOperationType getType();

}
