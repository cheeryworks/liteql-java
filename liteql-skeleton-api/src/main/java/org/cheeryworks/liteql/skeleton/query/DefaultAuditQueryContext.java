package org.cheeryworks.liteql.skeleton.query;

import org.cheeryworks.liteql.skeleton.model.UserType;

public class DefaultAuditQueryContext implements AuditQueryContext {

    private UserType user;

    public DefaultAuditQueryContext() {
    }

    public DefaultAuditQueryContext(UserType user) {
        this.user = user;
    }

    @Override
    public UserType getUser() {
        return this.user;
    }

}
