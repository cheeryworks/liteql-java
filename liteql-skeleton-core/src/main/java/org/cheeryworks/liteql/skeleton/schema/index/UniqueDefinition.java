package org.cheeryworks.liteql.skeleton.schema.index;

import org.cheeryworks.liteql.skeleton.schema.enums.IndexType;

public class UniqueDefinition extends AbstractIndexDefinition {

    public UniqueDefinition() {
        super(IndexType.Unique);
    }

}
