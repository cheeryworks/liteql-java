package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.model.UserType;

public class DefaultQueryContext implements QueryContext {

    private UserType user;

    public DefaultQueryContext(UserType user) {
        this.user = user;
    }

    @Override
    public UserType getUser() {
        return this.user;
    }
}
