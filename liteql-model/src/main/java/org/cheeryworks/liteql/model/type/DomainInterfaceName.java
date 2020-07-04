package org.cheeryworks.liteql.model.type;

import java.io.Serializable;

public class DomainInterfaceName implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DomainInterfaceName)) {
            return false;
        }

        DomainInterfaceName that = (DomainInterfaceName) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
