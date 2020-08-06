package org.cheeryworks.liteql.schema.index;

import org.cheeryworks.liteql.schema.enums.IndexType;

public class UniqueDefinition extends AbstractIndexDefinition {

    public UniqueDefinition() {
        super(IndexType.Unique);
    }

}
