package org.cheeryworks.liteql.schema.index;

import org.cheeryworks.liteql.schema.enums.IndexType;

public class Unique extends AbstractIndex {

    public Unique() {
        super(IndexType.Unique);
    }

}
