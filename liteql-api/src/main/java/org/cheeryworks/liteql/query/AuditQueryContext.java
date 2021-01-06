package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.model.UserType;

public interface AuditQueryContext extends QueryContext {

    UserType getUser();

}
