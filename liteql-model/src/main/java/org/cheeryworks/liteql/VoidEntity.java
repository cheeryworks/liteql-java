package org.cheeryworks.liteql;

import org.cheeryworks.liteql.schema.Entity;

public class VoidEntity implements Entity {

    @Override
    public String getId() {
        return null;
    }

}
