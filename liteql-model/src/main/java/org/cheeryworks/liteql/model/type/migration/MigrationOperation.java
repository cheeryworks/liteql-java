package org.cheeryworks.liteql.model.type.migration;

import java.io.Serializable;

public abstract class MigrationOperation implements Serializable {

    private String operation;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

}
