package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.schema.UserEntity;

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
