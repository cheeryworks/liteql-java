package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.ResourceDefinition;
import org.cheeryworks.liteql.util.LiteQLConstants;

@ResourceDefinition(schema = LiteQLConstants.SCHEMA)
public interface UserEntity extends Entity {

    @Position(1)
    String getName();

    @Position(2)
    String getUsername();

    @Position(3)
    String getEmail();

    @Position(4)
    String getPhone();

    @Position(5)
    String getAvatarUrl();

    @Position(6)
    boolean isEnabled();

}
