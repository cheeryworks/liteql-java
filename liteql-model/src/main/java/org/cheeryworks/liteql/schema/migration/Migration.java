package org.cheeryworks.liteql.schema.migration;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.operation.MigrationOperation;

import java.io.Serializable;
import java.util.List;

public class Migration implements Serializable {

    public static final String STATE_PENDING = "Pending";

    public static final String STATE_SUCCESS = "Success";

    public static final String STATE_FAILED = "Failed";

    private String name;

    private TypeName domainTypeName;

    private String version;

    private String description;

    private boolean baseline;

    private List<MigrationOperation> operations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isBaseline() {
        return baseline;
    }

    public void setBaseline(boolean baseline) {
        this.baseline = baseline;
    }

    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MigrationOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<MigrationOperation> operations) {
        this.operations = operations;
    }

}
