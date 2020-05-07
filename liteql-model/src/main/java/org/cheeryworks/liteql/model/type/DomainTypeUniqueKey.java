package org.cheeryworks.liteql.model.type;

import java.io.Serializable;
import java.util.List;

public class DomainTypeUniqueKey implements Serializable {

    private List<String> fields;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

}
