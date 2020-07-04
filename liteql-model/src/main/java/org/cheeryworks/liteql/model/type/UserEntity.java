package org.cheeryworks.liteql.model.type;

public interface UserEntity extends DomainInterface {

    String getId();

    String getName();

    String getUsername();

    String getEmail();

    String getPhone();

    String getAvatarUrl();

    boolean isEnabled();

}
