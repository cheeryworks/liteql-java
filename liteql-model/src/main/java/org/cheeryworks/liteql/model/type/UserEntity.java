package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.annotation.Position;
import org.cheeryworks.liteql.model.annotation.ResourceDefinition;
import org.cheeryworks.liteql.model.util.LiteQLConstants;

@ResourceDefinition(namespace = LiteQLConstants.NAMESPACE)
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
