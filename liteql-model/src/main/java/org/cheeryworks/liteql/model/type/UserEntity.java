package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.annotation.ResourceDefinition;
import org.cheeryworks.liteql.model.util.LiteQLConstants;

@ResourceDefinition(namespace = LiteQLConstants.NAMESPACE)
public interface UserEntity extends Entity {

    String getName();

    String getUsername();

    String getEmail();

    String getPhone();

    String getAvatarUrl();

    boolean isEnabled();

}
