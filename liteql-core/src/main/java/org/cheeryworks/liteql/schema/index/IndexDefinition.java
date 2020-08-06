package org.cheeryworks.liteql.schema.index;

import org.cheeryworks.liteql.schema.enums.IndexType;

public class IndexDefinition extends AbstractIndexDefinition {

    public IndexDefinition() {
        super(IndexType.Normal);
    }
}
