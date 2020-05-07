package org.cheeryworks.liteql.model.migration;

import java.io.Serializable;

public abstract class MigrationOperation implements Serializable {

    private String operation;

    private String domainType;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

}
