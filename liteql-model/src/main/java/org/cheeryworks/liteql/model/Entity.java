package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.Trait;
import org.cheeryworks.liteql.schema.annotation.Type;

@Type
public interface Entity extends Trait {

    String getId();

}
