package org.cheeryworks.liteql.skeleton.service.query;

import org.cheeryworks.liteql.skeleton.model.UserType;
import org.cheeryworks.liteql.skeleton.query.AbstractDomainQuery;

public interface QueryAccessDecisionService {

    void decide(UserType user, AbstractDomainQuery query);

}
