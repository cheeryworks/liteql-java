package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.schema.annotation.LiteQLType;

@LiteQLType
public interface UserType extends DomainType {

    @LiteQLFieldPosition(1)
    String getName();

    @LiteQLFieldPosition(2)
    String getUsername();

    @LiteQLFieldPosition(3)
    String getEmail();

    @LiteQLFieldPosition(4)
    String getPhone();

    @LiteQLFieldPosition(5)
    String getAvatarUrl();

    @LiteQLFieldPosition(6)
    boolean isEnabled();

}
