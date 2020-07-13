package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.model.type.UserEntity;

public class DefaultQueryContext implements QueryContext {

    private UserEntity user;

    public DefaultQueryContext(UserEntity user) {
        this.user = user;
    }

    @Override
    public UserEntity getUser() {
        return this.user;
    }
}
