package org.cheeryworks.liteql.schema.index;

import org.cheeryworks.liteql.schema.enums.IndexType;

public class Index extends AbstractIndex {

    public Index() {
        super(IndexType.Normal);
    }
}
