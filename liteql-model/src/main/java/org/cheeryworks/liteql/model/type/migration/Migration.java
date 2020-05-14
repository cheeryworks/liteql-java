package org.cheeryworks.liteql.model.type.migration;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

public class Migration implements Serializable {

    public static final String STATE_PENDING = "Pending";

    public static final String STATE_SUCCESS = "Success";

    public static final String STATE_FAILED = "Failed";

    private String name;

    private String schema;

    private String domainType;

    private String description;

    private List<MigrationOperation> operations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        if (StringUtils.isNotBlank(name)) {
            return name.split("__")[0];
        }

        return null;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getDescription() {
        if (StringUtils.isBlank(description) && StringUtils.isNotBlank(name)) {
            return name.split("__")[1];
        }

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
