package org.cheeryworks.liteql.model.type.index;

import org.cheeryworks.liteql.model.enums.IndexType;

public class Unique extends AbstractIndex {

    public Unique() {
        super(IndexType.Unique);
    }

}
