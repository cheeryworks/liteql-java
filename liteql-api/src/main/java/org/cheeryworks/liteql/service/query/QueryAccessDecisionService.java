package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.AbstractDomainQuery;
import org.cheeryworks.liteql.model.UserType;

public interface QueryAccessDecisionService {

    default void decide(UserType user, AbstractDomainQuery query) {

    }

}
