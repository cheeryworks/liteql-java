package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.schema.annotation.ResourceDefinition;
import org.cheeryworks.liteql.util.LiteQLConstants;

@ResourceDefinition(schema = LiteQLConstants.SCHEMA)
public interface Entity extends Trait {

    String getId();

}
