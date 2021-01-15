package org.cheeryworks.liteql.skeleton.query;

import org.cheeryworks.liteql.skeleton.model.UserType;

public interface AuditQueryContext extends QueryContext {

    UserType getUser();

}
